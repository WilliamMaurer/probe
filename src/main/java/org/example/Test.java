package org.example;

import javassist.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.lang.instrument.ClassFileTransformer;
import java.security.ProtectionDomain;
import java.util.Arrays;

public class Test implements ClassFileTransformer {

    public static void logException(CtMethod[] arr,CtClass cl) throws CannotCompileException, NotFoundException {
        cl.addMethod(CtMethod.make(getProcessId,cl));
        cl.addMethod(CtMethod.make(logExceptionAndCache, cl));
        CtClass etype = ClassPool.getDefault().get("java.lang.Exception");
        for (CtMethod method:arr
        ) {
            method.addCatch("{java.lang.String strings= $e.toString();" +
                    "java.lang.String Id=getProcessId();" +
                    "long start = System.currentTimeMillis();" +
                    "java.lang.String[] arr = {Id,strings, java.lang.String.valueOf(start)};" +
                    "java.lang.String log_res = logExceptionAndCache(\"logException.txt\",start,Id,strings);" +
                    "java.lang.String log_res1 = logExceptionAndCache(\"logExceptionCache.txt\",start,Id,strings);" +
                    " throw $e; }", etype);
        }
    }
    public static void logExceptionDriver(CtMethod[] arr,CtClass cl) throws CannotCompileException, NotFoundException {

        CtClass etype = ClassPool.getDefault().get("java.lang.Exception");
        for (CtMethod method:arr
        ) {
            method.addCatch("{java.lang.String strings= $e.toString();" +
                    "java.lang.String Id=getProcessId();" +
                    "long start = System.currentTimeMillis();" +
                    "java.lang.String log_res = logExceptionAndCache(\"logException.txt\",start,Id,strings);" +
                    //            "java.lang.String log_res1 = logExceptionAndCache(\"logExceptionCache.txt\",start,Id,strings);" +
                    " throw $e; }", etype);
        }
    }

    static String readlog="public static java.lang.String readLog()throws Exception{\n" +
            "            java.io.File file = new java.io.File(\"logCache.txt\");//Text文件bai\n" +
            "            java.io.BufferedReader br = new java.io.BufferedReader(new  java.io.FileReader(file));//构造一个duBufferedReader类来读取zhi文件\n" +
            "            java.lang.String s = null;\n" +
            "            java.lang.StringBuffer sb=new java.lang.StringBuffer();\n" +
            "            while((s = br.readLine())!=null){\n" +
            "                System.out.println(s);\n" +
            "            }\n" +
            "            br.close();;\n" +
            "            return sb.toString();\n" +
            "        }";
    static String logExceptionAndCache=("public static String logExceptionAndCache(java.lang.String file,long startTime,java.lang.String processID,java.lang.String Exception)" +
            "{\n"+
            "        java.lang.String filePath =file;\n"+
            "        java.lang.String message = java.lang.Long.toString(startTime)+\",\"+processID+\",\"+Exception;" +
            "        java.lang.String res = \"\";\n" +
            "        java.io.FileWriter fw = null;\n" +
            "        java.io.PrintWriter pw = null;\n" +
            "        java.io.File f = new java.io.File(filePath);\n" +
            "        try {\n" +
            "            fw = new java.io.FileWriter(f,true);\n" +
            "            pw = new java.io.PrintWriter(fw);\n" +
            "            pw.println(message);\n" +
            "        } catch (java.io.IOException e) {\n" +
            "            e.printStackTrace();\n" +
            "            System.out.println(\"写入txt文件出现异常！\");\n" +
            "        }finally {\n" +
            "            try {\n" +
            "                pw.flush();\n" +
            "                fw.flush();\n" +
            "                pw.close();\n" +
            "                fw.close();\n" +
            "                res = \"log out successed\";\n" +
            "            } catch (java.io.IOException e) {\n" +
            "                e.printStackTrace();\n" +
            "                System.out.println(\"在刷新/关闭txt文件出现异常！\");\n" +
            "                res = \"log out failed\";\n" +
            "            }\n" +
            "        }\n" +
            "        return res;\n" +
            "    }");

    static String getProcessId= "public static String getProcessId()" +
            "{java.lang.management.RuntimeMXBean runtimeMXBean " +
            "= java.lang.management.ManagementFactory.getRuntimeMXBean(); " +
            "return (runtimeMXBean.getName().split(\"@\")[0]);}";

    static String Stringlog=("public static String writeLog(long startTime,java.lang.String processID,java.lang.String sql,long costTime)" +
            "{\n"+
            "        java.lang.String filePath = \"Log.txt\";\n"+
            "        java.lang.String message = java.lang.Long.toString(startTime)+\",\"+processID+\",\"+sql+\",\"+java.lang.Long.toString(costTime);" +
            "        java.lang.String res = \"\";\n" +
            "        java.io.FileWriter fw = null;\n" +
            "        java.io.PrintWriter pw = null;\n" +
            "        java.io.File f = new java.io.File(filePath);\n" +
            "        try {\n" +
            "            fw = new java.io.FileWriter(f,true);\n" +
            "            pw = new java.io.PrintWriter(fw);\n" +
            "            pw.println(message);\n" +
            "        } catch (java.io.IOException e) {\n" +
            "            e.printStackTrace();\n" +
            "            System.out.println(\"写入txt文件出现异常！\");\n" +
            "        }finally {\n" +
            "            try {\n" +
            "                pw.flush();\n" +
            "                fw.flush();\n" +
            "                pw.close();\n" +
            "                fw.close();\n" +
            "                res = \"log out successed\";\n" +
            "            } catch (java.io.IOException e) {\n" +
            "                e.printStackTrace();\n" +
            "                System.out.println(\"在刷新/关闭txt文件出现异常！\");\n" +
            "                res = \"log out failed\";\n" +
            "            }\n" +
            "        }\n" +
            "        return res;\n" +
            "    }");


    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer)  {

        ClassPool pool = ClassPool.getDefault();
//        String newName = className.replace("/",".");
        /*
        * 1. preparedStatements通过java.sql.Connnection的(prepareCall|Statement)方法进行SQL传递,然后通过PreparedStatement.executeQuery()执行SQL语句；
        * 2. statement通过java.sql.Statement的execute($|Update|Query|Batch)方法传递SQL并执行SQL语句；
        * 3. mysql-connector 驱动是通过类名：com.mysql.cj.jdbc.StatementImpl和类名：com.mysql.cj.jdbc.ConnectionImpl实现的,是通过Statement实现的
        *
        * */



        try {
            CtClass cl = pool.makeClass(new ByteArrayInputStream(classfileBuffer));

            if (( Arrays.toString(cl.getInterfaces()).contains("java.sql.Statement")||Arrays.toString(cl.getInterfaces()).contains("java.sql.Connection")
                    ||Arrays.toString(cl.getInterfaces()).contains("java.sql.PrepareStatement"))&& !cl.isInterface()){
                /*
                * mysql-connector执行SQL主要通过com.mysql.cj.jdbc.ConnectionImpl和com.mysql.cj.jdbc.StatementImpl实现
                * mysql-connector在执行Statement对象的
                * */
                CtMethod[] ctMethods = cl.getDeclaredMethods();
                logException(ctMethods,cl);
                String clazzName = cl.getName();
//                判断是通过那个方法执行SQL的
                String curStatement = currentStatement(ctMethods);
//                System.out.println(curStatement);
                if (curStatement.equals("PreparedStatement")){
//                    通过PreparedStatement方式执行SQL的
                    captureSqlFromPreparedStatement(pool,cl);
//                    CtClass esqlclazz = SqlExecuteTime(pool,cl);
//                    byte[] epbytes = esqlclazz.toBytecode();

                }else if(curStatement.equals("Statement")){
//                    通过Statement方式执行SQL的
                    captureSqlFromStatement(pool,cl);
                    byte[] ddbytes =cl.toBytecode();
                    return ddbytes;

                }else {
                    System.out.println("不能判断是通过PreparedStatement对象还是Statement对象实现SQL语句的。");
                }
            } else if(className.equals("com/mysql/cj/jdbc/ClientPreparedStatement")) {
                CtMethod ad = cl.getDeclaredMethod("getInstance");
                ad.addLocalVariable("sql", pool.get("java.lang.String"));
                ad.insertBefore("sql = $2;");
                ad.insertAfter("org.example.MonitorLog.setSql(sql);");
                ad.insertAfter("System.out.println(\"<------A PreparedStatement object. This step is for SQL. Achieved by com/mysql/cj/jdbc/ClientPreparedStatement.------>\");");
                ad.insertAfter("System.out.println(\"sql:\"+sql);");

                CtMethod ct = cl.getDeclaredMethod("executeQuery");
                ct.addLocalVariable("id", pool.get("java.lang.String"));
                ct.insertBefore("id = org.example.ProcessId.getProcessId();");

                ct.addLocalVariable("start", CtClass.longType);
                ct.insertBefore("start = System.currentTimeMillis();");

                ct.addLocalVariable("cost", CtClass.longType);
                ct.insertAfter("cost = System.currentTimeMillis()-start;");

                ct.insertAfter("System.out.println(\"<------A PreparedStatement executeQuery(). Achieved by com/mysql/cj/jdbc/ClientPreparedStatement.------>\");");
                ct.insertAfter("System.out.println(\"start:\"+start);");
                ct.insertAfter("System.out.println(\"processID:\"+id);");
                ct.insertAfter("System.out.println(\"cost:\"+cost);");

                ct.insertAfter("org.example.MonitorLog.setStart(start);");
                ct.insertAfter("org.example.MonitorLog.setCost(cost);");
                ct.insertAfter("org.example.MonitorLog.setProcessID(id);");

                ct.addLocalVariable("log_res", pool.get("java.lang.String"));
//                ct.insertAfter("log_res = org.example.MonitorLog.writeLog(o_start,o_processID,o_sql,o_cost);");


                ct.insertAfter("log_res = org.example.MonitorLog.tests();");

                //增删操作：
                CtMethod inserDelete = cl.getDeclaredMethod("executeUpdate");
                inserDelete.addLocalVariable("id", pool.get("java.lang.String"));
                inserDelete.insertBefore("id = org.example.ProcessId.getProcessId();");

                inserDelete.addLocalVariable("start", CtClass.longType);
                inserDelete.insertBefore("start = System.currentTimeMillis();");


                inserDelete.addLocalVariable("cost", CtClass.longType);
                inserDelete.insertAfter("cost = System.currentTimeMillis()-start;");

                inserDelete.insertAfter("System.out.println(\"<------A PreparedStatement executeUpdate(). Achieved by com/mysql/cj/jdbc/ClientPreparedStatement.------>\");");
                inserDelete.insertAfter("System.out.println(\"start:\"+start);");
                inserDelete.insertAfter("System.out.println(\"processID:\"+id);");
                inserDelete.insertAfter("System.out.println(\"cost:\"+cost);");

                inserDelete.insertAfter("org.example.MonitorLog.setStart(start);");
                inserDelete.insertAfter("org.example.MonitorLog.setCost(cost);");
                inserDelete.insertAfter("org.example.MonitorLog.setProcessID(id);");

                inserDelete.addLocalVariable("log_res", pool.get("java.lang.String"));
//                ct.insertAfter("log_res = org.example.MonitorLog.writeLog(o_start,o_processID,o_sql,o_cost);");


                inserDelete.insertAfter("log_res = org.example.MonitorLog.tests();");

                byte[] ddbytes =cl.toBytecode();
                return ddbytes;
            }
            else if(className.equals("java/sql/DriverManager")) {
                //<..............异常落入日志.......>
                cl.addMethod(CtMethod.make(getProcessId,cl));
                cl.addMethod(CtMethod.make(logExceptionAndCache, cl));
                cl.addMethod(CtMethod.make(Stringlog, cl));
                CtMethod[] arr = cl.getDeclaredMethods();
                logExceptionDriver(arr,cl);

                byte[] dmbytes = JudgeDriver(pool,cl).toBytecode();
                //driver发送到kafka
                String logCache=MonitorLog.sendDriverMessage("logCache.txt");
                if (logCache!=null){
                    MonitorLog.postToKafa( "http://113.106.111.75:5040/demo/kafka/produce" ,MonitorLog.strTojson1(logCache.split(",")));
                }

                return dmbytes;
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
/*
* 获取DriverManager参数
* */
    public static CtClass JudgeDriver(ClassPool pool,CtClass ctClass) throws CannotCompileException, NotFoundException {
        CtMethod ct = ctClass.getDeclaredMethod("getConnection",
                new CtClass[]{pool.get("java.lang.String"),pool.get("java.util.Properties"),pool.get("java.lang.Class")});
        ct.addLocalVariable("clazz",pool.get("java.lang.String"));
        ct.addLocalVariable("info",pool.get("java.util.Properties"));
        ct.addLocalVariable("url",pool.get("java.lang.String"));
        ct.addLocalVariable("strings",pool.get("java.lang.String"));
        ct.insertBefore("url=$1;");
        ct.insertBefore("info=$2;");
        ct.insertBefore("clazz=$3;");
        ct.insertAfter("strings=url+\" \"+info.toString().split(\",\")[0]+\" \"+$3.toString();");
        ct.addLocalVariable("id",pool.get("java.lang.String"));
        ct.addLocalVariable("start",CtClass.longType);

        ct.insertAfter("id = getProcessId();");
        ct.insertBefore("start = System.currentTimeMillis();");
        ct.addLocalVariable("cost",CtClass.longType);
        ct.insertAfter("cost = System.currentTimeMillis()-start;");
        ct.insertAfter("System.out.println(\"<------It is a DriverManager:------>\");");
        ct.insertAfter("System.out.println(\"processID:\"+id);");
        ct.insertAfter("System.out.println(\"cost:\"+cost);");
        ct.addLocalVariable("log_res",pool.get("java.lang.String"));
        ct.insertAfter("log_res = writeLog(start,id,strings,cost);");
        ct.addLocalVariable("log_res1",pool.get("java.lang.String"));
        ct.insertAfter( " log_res1 = logExceptionAndCache(\"logCache.txt\",start,id,strings);" );

        return ctClass;
    }
    /*
    * 如果 SQL语句 是通过 PrepareStatement实例化对象，需要通过 connection.prepareStatement（）或 connection.prepareCall() 获取SQL参数
    * */
    public static void captureSqlFromPreparedStatement(ClassPool pool, CtClass clazz) throws NotFoundException, CannotCompileException {



//        captureSqlFromPreparCall(pool,clazz);
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

        pm.insertAfter("System.out.println(\"<------A PreparedStatement Object. This is prepareStatement() for SQL:------>\");");
        pm.insertAfter("System.out.println(\"start:\"+start);");
        pm.insertAfter("System.out.println(\"processID:\"+id);");
        pm.insertAfter("System.out.println(\"sql:\"+sql);");
        pm.insertAfter("System.out.println(\"cost:\"+cost);");
        pm.insertAfter("org.example.MonitorLog.setSql(sql);");
        pm.insertAfter("org.example.MonitorLog.setProcessID(id);");


//        SqlExecuteTime(pool,clazz);

//        输出至日志文档
        pm.addLocalVariable("log_res",pool.get("java.lang.String"));
//        pm.insertAfter("log_res = org.example.MonitorLog.tests();");
        pm.insertAfter("log_res = org.example.MonitorLog.writeLog(start,id,sql,cost);");

//        return clazz;
    }
    public static CtClass captureSqlFromPreparCall(ClassPool pool, CtClass clazz) throws NotFoundException, CannotCompileException {
//        System.out.println("这是个PreparedStatement对象。");
        CtMethod pm = clazz.getDeclaredMethod("prepareCall",new CtClass[]{pool.get("java.lang.String")});
//        获取SQL语句
        pm.addLocalVariable("sql",pool.get("java.lang.String"));
        pm.insertBefore("sql = $1;");


//        获取进程ID
        pm.addLocalVariable("id",pool.get("java.lang.String"));
        pm.insertBefore("id = org.example.ProcessId.getProcessId();");
        pm.insertBefore("org.example.MonitorLog.setProcessID(id);");

//        获取SQL执行的开始时间
        pm.addLocalVariable("start",CtClass.longType);
        pm.insertBefore("start = System.currentTimeMillis();");
//        获取SQL的运行时间
        pm.addLocalVariable("cost",CtClass.longType);
        pm.insertAfter("cost = System.currentTimeMillis()-start;");

        pm.insertAfter("System.out.println(\"<------A PreparedStatement Object. This is prepareCall() for SQL:------>\");");
        pm.insertAfter("System.out.println(\"start:\"+start);");
        pm.insertAfter("System.out.println(\"processID:\"+id);");
        pm.insertAfter("System.out.println(\"sql:\"+sql);");
        pm.insertAfter("System.out.println(\"cost:\"+cost);");
        pm.insertAfter("org.example.MonitorLog.setSql(sql);");

//        SqlExecuteTime(pool,clazz);
//        输出至日志文档
        
        pm.addLocalVariable("log_res",pool.get("java.lang.String"));
        pm.insertAfter("log_res = org.example.MonitorLog.writeLog(start,id,sql,cost);");

        return clazz;
    }

    /*
    * 如果 SQL语句是通过Statement实例化对象执行的，需要通过statement.executeQuery()获取SQL参数
    * */
    public static void captureSqlFromStatement(ClassPool pool,CtClass clazz) throws NotFoundException, CannotCompileException {
//        System.out.println("这是个Statement对象。");

        try {
            CtMethod em = clazz.getDeclaredMethod("executeQuery", new CtClass[]{pool.get("java.lang.String")});
//        获取SQL语句
            em.addLocalVariable("sql", pool.get("java.lang.String"));
            em.insertBefore("sql = $1;");


//        获取进程ID
            em.addLocalVariable("id", pool.get("java.lang.String"));
            em.insertBefore("id = org.example.ProcessId.getProcessId();");


//        获取SQL执行的开始时间
            em.addLocalVariable("start", CtClass.longType);
            em.insertBefore("start = System.currentTimeMillis();");


//        获取SQL的运行时间
            em.addLocalVariable("cost", CtClass.longType);
            em.insertAfter("cost = System.currentTimeMillis()-start;");
            em.insertAfter("System.out.println(\"<------A Statement Object. This is execute() for SQL. Achieved by com/mysql/cj/jdbc/StatementImpl:------>\");");
            em.insertAfter("System.out.println(\"start:\"+start);");
            em.insertAfter("System.out.println(\"processID:\"+id);");
            em.insertAfter("System.out.println(\"sql:\"+sql);");
            em.insertAfter("System.out.println(\"cost:\"+cost);");

//        输出至日志文档
            em.insertAfter("org.example.MonitorLog.setSql(sql);");
            em.insertAfter("org.example.MonitorLog.setCost(cost);");
            em.insertAfter("org.example.MonitorLog.setStart(start);");
            em.insertAfter("org.example.MonitorLog.setProcessID(id);");
            em.addLocalVariable("log_res", pool.get("java.lang.String"));
//        em.insertAfter("log_res = org.example.MonitorLog.writeLog(start,id,sql,cost);");

            em.insertAfter("log_res = org.example.MonitorLog.tests();");
        }catch ( NotFoundException |CannotCompileException e){
            System.out.println("在探测Statement对象时，没有executeQuery(String sql)获取探测信息。");
        }
        try{
            //增删操作：
            CtMethod inserDelete = clazz.getDeclaredMethod("executeUpdate", new CtClass[]{pool.get("java.lang.String")});
            inserDelete.addLocalVariable("id", pool.get("java.lang.String"));
            inserDelete.insertBefore("id = org.example.ProcessId.getProcessId();");

            inserDelete.addLocalVariable("sql", pool.get("java.lang.String"));
            inserDelete.insertBefore("sql = $1;");

            inserDelete.addLocalVariable("start", CtClass.longType);
            inserDelete.insertBefore("start = System.currentTimeMillis();");


            inserDelete.addLocalVariable("cost", CtClass.longType);
            inserDelete.insertAfter("cost = System.currentTimeMillis()-start;");

            inserDelete.insertAfter("System.out.println(\"<------A Statement executeUpdate(). Achieved by com/mysql/cj/jdbc/StatementImpl.------>\");");
            inserDelete.insertAfter("System.out.println(\"start:\"+start);");
            inserDelete.insertAfter("System.out.println(\"processID:\"+id);");
            inserDelete.insertAfter("System.out.println(\"cost:\"+cost);");
            inserDelete.insertAfter("System.out.println(\"sql:\"+sql);");

            inserDelete.insertAfter("org.example.MonitorLog.setStart(start);");
            inserDelete.insertAfter("org.example.MonitorLog.setCost(cost);");
            inserDelete.insertAfter("org.example.MonitorLog.setProcessID(id);");
            inserDelete.insertAfter("org.example.MonitorLog.setSql(sql);");

            inserDelete.addLocalVariable("log_res", pool.get("java.lang.String"));
            inserDelete.insertAfter("log_res = org.example.MonitorLog.tests();");
        }catch (NotFoundException |CannotCompileException e){
            System.out.println("在探测Statement对象时，没有executeUpdate(String sql)获取探测信息。");
        }
//        return clazz;
    }
    public static void SqlExecuteTime(ClassPool pool,CtClass clazz) throws NotFoundException, CannotCompileException {

        CtMethod closeStatementm = clazz.getDeclaredMethod("com.mysql.cj.jdbc.ClientPreparedStatement.executeQuery");
//        获取SQL的运行时间
        closeStatementm.addLocalVariable("end",CtClass.longType);
        closeStatementm.insertBefore("end = System.currentTimeMillis();");
        closeStatementm.insertBefore("System.out.println(\"<------This is execute() for end:------>\");");
        closeStatementm.insertBefore("System.out.println(\"end:\"+end);");

//        return cpsclazz;
    }
//    public static void sqlExecute(ClassPool pool,CtClass clazz) throws NotFoundException, CannotCompileException {
//
//        CtMethod sqlem = clazz.getDeclaredMethod("execute");
//
////        获取SQL的运行时间
//        sqlem.addLocalVariable("end",CtClass.longType);
//        sqlem.insertAfter("end = System.currentTimeMillis();");
//        sqlem.insertAfter("System.out.println(\"<------This is execute() for end:------>\");");
//        sqlem.insertAfter("System.out.println(\"end:\"+end);");
//
//    }
//    public static void sqlExecuteUpdate(ClassPool pool,CtClass clazz) throws NotFoundException, CannotCompileException {
//
//        CtMethod sqleum = clazz.getDeclaredMethod("executeUpdate");
////        获取SQL的运行时间
//        sqleum.addLocalVariable("end",CtClass.longType);
//        sqleum.insertAfter("end = System.currentTimeMillis();");
//        sqleum.insertAfter("System.out.println(\"<------This is executeUpdate() for end:------>\");");
//        sqleum.insertAfter("System.out.println(\"end:\"+end);");
//    }
//    public static void sqlExecuteBatch(ClassPool pool,CtClass clazz) throws NotFoundException, CannotCompileException {
//
//        CtMethod sqlebm = clazz.getDeclaredMethod("executeBatch");
////        获取SQL的运行时间
//        sqlebm.addLocalVariable("end",CtClass.longType);
//        sqlebm.insertAfter("end = System.currentTimeMillis();");
//        sqlebm.insertAfter("System.out.println(\"<------This is executeBatch() for end:------>\");");
//        sqlebm.insertAfter("System.out.println(\"end:\"+end);");
//    }

    /*
    * 测试 hellw类 中的 add()方法
    * */
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
    /*
    * 判断当前语句执行实例是 Statement 还是 PreparedStatement
    * */
    public static String currentStatement(CtMethod[] ctMethods) throws NotFoundException {
        boolean isStatement=false;
        boolean isPreparedStatement=false;
        String res = "";
        for(CtMethod cm:ctMethods){
            if(cm.getName().contains("execute")&&(cm.getParameterTypes().length==1)){
                isStatement = true;
            }
            if(cm.getName().contains("prepare")&&(cm.getParameterTypes().length==0)){
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
