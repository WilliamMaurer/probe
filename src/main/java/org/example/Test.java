package org.example;

import javassist.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.lang.instrument.ClassFileTransformer;
import java.security.ProtectionDomain;
import java.util.Arrays;

public class Test implements ClassFileTransformer {

    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer)  {

        ClassPool pool = ClassPool.getDefault();
//        String newName = className.replace("/",".");
        /*
        * 1. preparedStatements通过java.sql.Connnection的(prepareCall|Statement)方法进行SQL传递,然后通过PreparedStatement.executeQuery()执行SQL语句；
        * 2. statement通过java.sql.Statement的execute($|Update|Query|Batch)方法传递SQL并执行SQL语句；
        * 3. mysql-connector 驱动是通过类名：com.mysql.cj.jdbc.StatementImpl和类名：com.mysql.cj.jdbc.ConnectionImpl实现的,是通过Statement实现的
        * */
        try {
            CtClass cl = pool.makeClass(new ByteArrayInputStream(classfileBuffer));

            if ((Arrays.toString(cl.getInterfaces()).equals("java.sql.PreparedStatement") || Arrays.toString(cl.getInterfaces()).contains("java.sql.Statement")
                    || Arrays.toString(cl.getInterfaces()).contains("java.sql.Connection") )&& !cl.isInterface()){

//                mysql-connector执行SQL主要通过com.mysql.cj.jdbc.ConnectionImpl和com.mysql.cj.jdbc.StatementImpl实现
                CtMethod[] ctMethods = cl.getDeclaredMethods();
                String clazzName = cl.getName();
//                判断是通过那个方法执行SQL的
                String curStatement = currentStatement(ctMethods);
                if (curStatement.equals("PreparedStatement")){
//                    通过PreparedStatement方式执行SQL的
                    CtClass  pclazz= captureSqlFromPreparedStatement(pool,cl);
                    byte[] pbytes =pclazz.toBytecode();
                    return pbytes;
                }else if(curStatement.equals("Statement")){
//                    通过Statement方式执行SQL的
                    CtClass  sclazz= captureSqlFromStatement(pool,cl);
                    byte[] sbytes =sclazz.toBytecode();
                    return sbytes;
                }else {
                    System.out.println("不能判断是通过PreparedStatement对象还是Statement对象实现SQL语句的。");
                }
            }
        } catch (NotFoundException | IOException | CannotCompileException e) {
            e.printStackTrace();
        }

//        测试示例
//        if(newName.equals("org.example.hellw")){
//                System.out.println(newName);
//            try {
//                CtClass clazz = hellwTest(pool);
//                byte[] bytes =clazz.toBytecode();
//                return bytes;
//            } catch (NotFoundException e) {
//                e.printStackTrace();
//            } catch (CannotCompileException | IOException e) {
//                e.printStackTrace();
//            }
//        }
        return classfileBuffer;
    }

    public static CtClass captureSqlFromPreparedStatement(ClassPool pool, CtClass clazz) throws NotFoundException, CannotCompileException {
        System.out.println("这是个PreparedStatement对象。");
        CtMethod pm = clazz.getDeclaredMethod("prepareStatement",new CtClass[]{pool.get("java.lang.String")});
//        获取SQL语句
        pm.addLocalVariable("sql",pool.get("java.lang.String"));
        pm.insertBefore("sql = $1;");

//        获取进程ID
        pm.addLocalVariable("id",pool.get("java.lang.String"));
        pm.insertBefore("id = org.example.ProcessId.getProcessId();");

//        获取SQL执行的开始时间
        pm.addLocalVariable("start",CtClass.longType);
        pm.insertBefore("start = System.currentTimeMillis();");
//        获取SQL的运行时间
        pm.addLocalVariable("cost",CtClass.longType);
        pm.insertAfter("cost = System.currentTimeMillis()-start;");

        pm.insertAfter("System.out.println(\"start:\"+start);");
        pm.insertAfter("System.out.println(\"processID:\"+id);");
        pm.insertAfter("System.out.println(\"sql:\"+sql);");
        pm.insertAfter("System.out.println(\"cost:\"+cost);");

//        输出至日志文档
        pm.addLocalVariable("log_res",pool.get("java.lang.String"));
        pm.insertAfter("log_res = org.example.MonitorLog.writeLog(start,id,sql,cost);");

        return clazz;
    }

    public static CtClass hellwTest(ClassPool pool) throws NotFoundException, CannotCompileException {
        CtClass clazz = pool.get("org.example.hellw");
        System.out.println(clazz);
        CtMethod method = clazz.getDeclaredMethod("add",new CtClass[]{pool.get("java.lang.String"),pool.get("java.lang.String")});
        method.addLocalVariable("start",CtClass.longType);
        method.insertBefore("start = System.currentTimeMillis();");
        System.out.println(method.getName());
        method.insertAfter("org.example.MonitorLog.info3($1,\"./Log.txt\");");

        return clazz;
    }
    public static CtClass captureSqlFromStatement(ClassPool pool,CtClass clazz) throws NotFoundException, CannotCompileException {
        System.out.println("这是个Statement对象。");
        CtMethod em = clazz.getDeclaredMethod("execute",new CtClass[]{pool.get("java.lang.String")});
//        获取SQL语句
        em.addLocalVariable("sql",pool.get("java.lang.String"));
        em.insertBefore("sql = $1;");

//        获取进程ID
        em.addLocalVariable("id",pool.get("java.lang.String"));
        em.insertBefore("id = org.example.ProcessId.getProcessId();");

//        获取SQL执行的开始时间
        em.addLocalVariable("start",CtClass.longType);
        em.insertBefore("start = System.currentTimeMillis();");

//        获取SQL的运行时间
        em.addLocalVariable("cost",CtClass.longType);
        em.insertAfter("cost = System.currentTimeMillis()-start;");

        em.insertAfter("System.out.println(\"start:\"+start);");
        em.insertAfter("System.out.println(\"processID:\"+id);");
        em.insertAfter("System.out.println(\"sql:\"+sql);");
        em.insertAfter("System.out.println(\"cost:\"+cost);");

//        输出至日志文档
        em.addLocalVariable("log_res",pool.get("java.lang.String"));
        em.insertAfter("log_res = org.example.MonitorLog.writeLog(start,id,sql,cost);");

        return clazz;
    }
    public static String currentStatement(CtMethod[] ctMethods){
        boolean isStatement=false;
        boolean isPreparedStatement=false;
        String res = "";
        for(CtMethod cm:ctMethods){
            if(cm.getName().contains("execute")){
                isStatement = true;
            }
            if(cm.getName().contains("prepare")){
                isPreparedStatement = true;
            }
        }
        if((isStatement^isPreparedStatement)){
            if(isPreparedStatement){
                res = "PreparedStatement";
            }else if(isStatement){
                res ="Statement";
            }
        }else {
            System.out.println("currentStatement()方法失效，无法判断是PrepareStateMent还是Statement！");
            res = "noStatement";
        }
        return res;
    }

}
