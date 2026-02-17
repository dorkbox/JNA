/*
 * Copyright 2026 dorkbox, llc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

@file:Suppress("unused")

package dorkbox.jna

import javassist.ClassPool
import javassist.CtClass
import javassist.CtMethod
import javassist.CtNewMethod
import java.lang.reflect.Field
import java.lang.reflect.Modifier
import java.util.concurrent.atomic.*

abstract class FieldAccessor {
    @JvmField
    var declaredFields: Array<Field> = emptyArray()

    // the class methods here are rewritten using javassist.
    abstract fun get(index: Int, nativeObject: Any?): Any?

    abstract fun get(fieldName: String, nativeObject: Any?): Any?

    abstract fun init(clazz: Class<*>, index: Int)

    companion object {
        val version = JNA.version

        data class FieldInfo(val fieldName: String, val fieldType: Class<*>, val private: Boolean)

        private val noOpAccessor = object : FieldAccessor() {
            init {
                declaredFields = emptyArray()
            }
            override fun get(index: Int, nativeObject: Any?): Any? {
                return null
            }

            override fun get(fieldName: String, nativeObject: Any?): Any? {
                return null
            }

            override fun init(clazz: Class<*>, index: Int) {
            }
        }
        private val accessoryIndex = AtomicInteger(0)
        private const val DEBUG = false

        private val classToAccessor = mutableMapOf<String, FieldAccessor>()

        @Throws(Exception::class)
        fun caching(protectedClass: Class<*>): FieldAccessor {
            val protectedClassName = protectedClass.name
            synchronized(classToAccessor) {
                if (!classToAccessor.containsKey(protectedClassName)) {
                    classToAccessor[protectedClassName] = create(protectedClass)
                }
                return classToAccessor[protectedClassName]!!
            }
        }

        @Throws(Exception::class)
        fun caching(protectedClassName: String): FieldAccessor {
            synchronized(classToAccessor) {
                if (!classToAccessor.containsKey(protectedClassName)) {
                    classToAccessor[protectedClassName] = create(protectedClassName)
                }
                return classToAccessor[protectedClassName]!!
            }
        }

        @Throws(Exception::class)
        fun create(protectedClassName: String, fieldNames: List<String> = emptyList()): FieldAccessor {
            val clazz = Class.forName(protectedClassName)
            return create(clazz, fieldNames)
        }

        @Throws(Exception::class)
        fun create(protectedClass: Class<*>, fieldNames: List<String> = emptyList()): FieldAccessor {
            val pool = ClassPool.getDefault()
            val classSuffix = "\$Generated_" + accessoryIndex.getAndIncrement()

            val classFieldInfo = getFieldInfo(protectedClass)
            if (classFieldInfo.isEmpty()) {
                // don't bother with anything since there is nothing to access.
                return noOpAccessor
            }

            var classFieldsToMap = fieldNames
            if (classFieldsToMap.isEmpty()) {
                // if none are specified, then we get ALL the fields
                classFieldsToMap = classFieldInfo.map { it.fieldName }
            }

            // create the glue-class
            val glueClassName = createGlueClass(pool, protectedClass, classFieldInfo, classSuffix, classFieldsToMap)
            val accessor = createAccessor(pool, classFieldInfo, classSuffix, classFieldsToMap, glueClassName)

            @Suppress("UNCHECKED_CAST")
            accessor.declaredFields = arrayOfNulls<Field>(classFieldsToMap.size) as Array<Field>
            classFieldsToMap.forEachIndexed { index, fieldName ->
                accessor.init(protectedClass, index)
            }
            return accessor
        }

        private fun getFieldInfo(clazz: Class<*>): List<FieldInfo> {
            return clazz.declaredFields.filterNot{ it.name.startsWith("$") }.map { field ->
                FieldInfo(field.name, field.type, Modifier.isPrivate(field.modifiers))
            }
        }

        private fun forEach(classFieldInfo: List<FieldInfo>, fieldNames: List<String>, func: (Int, String, Class<*>) -> Unit): Boolean {
            fieldNames.forEachIndexed { index, fieldName ->
                val fieldInfo = classFieldInfo.first { it.fieldName == fieldName }
                func(index, fieldName, fieldInfo.fieldType)
            }
            return true
        }

        private fun forEachPrivate(classFieldInfo: List<FieldInfo>, fieldNames: List<String>, func: (Int, String, Class<*>) -> Unit): Boolean {
            fieldNames.forEachIndexed { index, fieldName ->
                val fieldInfo = classFieldInfo.firstOrNull { it.fieldName == fieldName }
                if (fieldInfo?.private == true) {
                    func(index, fieldName, fieldInfo.fieldType)
                }
            }
            return true
        }

        private fun forEachNotPrivate(classFieldInfo: List<FieldInfo>, fieldNames: List<String>, func: (Int, String, Class<*>) -> Unit): Boolean {
            fieldNames.forEachIndexed { index, fieldName ->
                val fieldInfo = classFieldInfo.firstOrNull { it.fieldName == fieldName }
                if (fieldInfo?.private == false) {
                    func(index, fieldName, fieldInfo.fieldType)
                }
            }
            return true
        }

        private fun createGlueClass(
            pool: ClassPool,
            protectedClass: Class<*>,
            classFieldInfo: List<FieldInfo>,
            classSuffix: String,
            fieldNames: List<String>
        ): String {

            val protectedClassName = protectedClass.name
            val gluePackage = protectedClassName.substringBeforeLast(".")
            val glueClassName = "$gluePackage.FieldAccessory$classSuffix"

            val dynamicClass: CtClass = pool.makeClass(glueClassName)
            var method: CtMethod

            // GET FIELD VALUE DIRECTLY BY INDEX
            var g = "public static Object getByIndex(int index, Object nativeComp) {\n"
            forEachNotPrivate(classFieldInfo, fieldNames) { index, fieldName, fieldType ->
                g +=        "if (index == $index) {\n"
                g +=            "$protectedClassName castValue = (${protectedClassName})nativeComp;\n"
                g +=            "Object value = " + when (fieldType) {
                                    Boolean::class.javaPrimitiveType -> "Boolean.valueOf(castValue.$fieldName);\n"
                                    Byte::class.javaPrimitiveType -> "Byte.valueOf(castValue.$fieldName);\n"
                                    Short::class.javaPrimitiveType -> "Short.valueOf(castValue.$fieldName);\n"
                                    Char::class.javaPrimitiveType -> "Character.valueOf(castValue.$fieldName);\n"
                                    Int::class.javaPrimitiveType -> "Integer.valueOf(castValue.$fieldName);\n"
                                    Long::class.javaPrimitiveType -> "Long.valueOf(castValue.$fieldName);\n"
                                    Float::class.javaPrimitiveType -> "Float.valueOf(castValue.$fieldName);\n"
                                    Double::class.javaPrimitiveType -> "Double.valueOf(castValue.$fieldName);\n"
                                    else -> "castValue;\n"
                }
                if (DEBUG) {
                g +=            "java.lang.System.err.println(\"$glueClassName : Getting by index : $index '\" + value + \"'\" );\n"
                }
                g +=            "return value;\n"
                g +=        "}\n"
            }

            g += "return null;\n}"

            method = CtNewMethod.make(g, dynamicClass)
            dynamicClass.addMethod(method)

            // GET FIELD VALUE BY REFLECTION
            g =  "public static Object getByField(java.lang.reflect.Field field, Object nativeComp) {\n"
            g +=    "Object value = field.get(nativeComp);\n"
            if (DEBUG) {
            g +=    "java.lang.System.err.println(\"$glueClassName : Getting by field: '\" + field.getName() + \"' :  value: \" + value);\n"
            }

            g +=    "return value;\n"
            g += "}"

            method = CtNewMethod.make(g, dynamicClass)
            dynamicClass.addMethod(method)

            //////////
            // INIT
            //////////

            g =  "public static java.lang.reflect.Field init(Class clazz, int index) {\n"
            forEach(classFieldInfo, fieldNames) { index, fieldName, _ ->
            g +=    "if (index == ${index}) {\n"
            g +=        "java.lang.reflect.Field field = clazz.getDeclaredField( \"$fieldName\" );\n"
            g +=        "field.setAccessible( true );\n"
            if (DEBUG) {
            g +=            "java.lang.System.err.println(\"$glueClassName : Updating access: $index for '$fieldName'\");\n"
            }
            g +=        "return field;\n"
            g +=    "}\n"
            }

//            forEachNotPrivate(classFieldInfo, fieldNames) { index, fieldName, _ ->
//            g +=    "if (index == ${index}) {\n"
//            if (DEBUG) {
//            g +=        "java.lang.System.err.println(\"$glueClassName : Skipping direct access: $index for '$fieldName'\");\n"
//            }
//            g +=        "return null;\n"
//            g +=    "}\n"
//            }

            g += " return null;\n}"

            method = CtNewMethod.make(g, dynamicClass)
            dynamicClass.addMethod(method)

            val dynamicClassBytes = dynamicClass.toBytecode()
            ClassUtils.defineClass(null, dynamicClassBytes)

            return glueClassName
        }

        fun createAccessor(
            pool: ClassPool,
            classFieldInfo: List<FieldInfo>,
            classSuffix: String,
            classFieldsToMap: List<String>,
            glueClassName: String
        ): FieldAccessor {
            // Create a unique class name
            val qualifiedName = FieldAccessor::class.qualifiedName
            val classFixerName = qualifiedName + classSuffix
            val classFixer = pool.makeClass(classFixerName)


            // Set the superclass
            val ctAbstractClass = pool.get(qualifiedName)
            classFixer.superclass = ctAbstractClass



            var body = "{\n"
            classFieldsToMap.forEachIndexed { index, fieldName ->
            body += "if ($1 == $index) {\n"
                if (classFieldInfo.first { it.fieldName == fieldName }.private) {
                    if (DEBUG) {
            body +=     "java.lang.System.err.println(\"$classFixerName : Getting by field: \" + $1 );\n"
                    }
            body +=     "return ${glueClassName}.getByField(declaredFields[$1], $2);\n"
                } else {
                    if (DEBUG) {
            body +=     "java.lang.System.err.println(\"$classFixerName : Getting by index: \" + $1 );\n"
            body +=     "Object value = ${glueClassName}.getByIndex($1, $2);\n"
            body +=     "java.lang.System.err.println(\"$classFixerName : Getting by index: \" + $1 + \", value: \" + value);\n"
                    } else {
            body +=     "Object value = ${glueClassName}.getByIndex($1, $2);\n"
                    }

            body +=     "return value;\n"
                }
            body += "}\n"
            }
            body +="return null; }"

            val getMethods = ctAbstractClass.getDeclaredMethods("get")
//            getMethods.map { it.parameterTypes[0].name }
            var method = getMethods.first { it.parameterTypes[0].name == "int" }
            var newMethod = CtNewMethod.make(
                method.returnType,
                method.name,
                method.parameterTypes,
                method.getExceptionTypes(),
                body,
                classFixer
            )
            classFixer.addMethod(newMethod)

            // perform pre-verification for the modified method
            newMethod.getMethodInfo().rebuildStackMapForME(pool)





            body = "{\n"
            classFieldsToMap.forEachIndexed { index, fieldName ->
            body += "if (\"$fieldName\".equals($1)) {\n"
                if (classFieldInfo.first { it.fieldName == fieldName }.private) {
                    if (DEBUG) {
            body +=     "java.lang.System.err.println(\"$classFixerName : Getting by string -> field: \" + $1 + \", int: \" + $index);\n"
                    }
            body +=     "return ${glueClassName}.getByField(declaredFields[$index], $2);\n"
                } else {
                    if (DEBUG) {
            body +=     "java.lang.System.err.println(\"$classFixerName : Getting by string -> index: \" + $1  + \", int: \" + $index);\n"
            body +=     "Object value = ${glueClassName}.getByIndex($index, $2);\n"
            body +=     "java.lang.System.err.println(\"$classFixerName : Getting by string -> index: \" + $1  + \", int: \" + $index + \", value: \" + value);\n"
                    } else {
            body +=     "Object value = ${glueClassName}.getByIndex($index, $2);\n"
                    }

            body +=     "return value;\n"
                }
            body += "}\n"
            }
            body +="return null; }"

            method = getMethods.first { it.parameterTypes[0].name == "java.lang.String" }
            newMethod = CtNewMethod.make(
                method.returnType,
                method.name,
                method.parameterTypes,
                method.getExceptionTypes(),
                body,
                classFixer
            )
            classFixer.addMethod(newMethod)

            // perform pre-verification for the modified method
            newMethod.getMethodInfo().rebuildStackMapForME(pool)



            method = ctAbstractClass.getDeclaredMethod("init")
//            val size = classFieldsToMap.size
            body =
                "{" +
                    "java.lang.reflect.Field field = ${glueClassName}.init($1, $2);" +
//                    "java.lang.System.err.println(\"$classFixerName : init return ($size) \" + field  + \", index: \" + $2);\n" +
                    "declaredFields[$2] = field;" +
                "}"
            newMethod = CtNewMethod.make(
                method.returnType,
                method.name,
                method.parameterTypes,
                method.getExceptionTypes(),
                body,
                classFixer
            )
            classFixer.addMethod(newMethod)

            // perform pre-verification for the modified method
            newMethod.getMethodInfo().rebuildStackMapForME(pool)

            val classFixerBytes = classFixer.toBytecode()
            ClassUtils.defineClass(ClassLoader.getSystemClassLoader(), classFixerBytes)

            val clazz = Class.forName(classFixer.name)

            // Convert to class and return
            return clazz.getDeclaredConstructor().newInstance() as FieldAccessor
        }
    }
}
