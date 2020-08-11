package org.example;

import com.alibaba.fastjson.JSONObject;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;

/*
* 负责输出日志，尝试用java原生环境的方法
* */
public class MonitorLog {

    public static final String url = "http://113.106.111.75:5040/demo/kafka/produce";

    public static long start;
    public static long cost;
    public static String sql = "";
    public static String processID = "";
    public static String logPath="Log.txt";
    //日志开关
    public static String logSwitch = "1";

    public static String getProcessID() {
        return processID;
    }

    public static void setProcessID(String processID) {
        MonitorLog.processID = processID;
    }

    public static String getSql() {
        return sql;
    }

    public static void setSql(String sql) {
        MonitorLog.sql = sql;
    }

    public static long getCost() {
        return cost;
    }

    public static void setCost(long cost) {
        MonitorLog.cost = cost;
    }

    public static long getStart() {
        return start;
    }

    public static void setStart(long start) {
        MonitorLog.start = start;
    }

    public static String getLogPath() {
        return logPath;
    }

    public static void setLogPath(String logPath) {
        MonitorLog.logPath = logPath;
    }

    /*
     * 输出Log至Log.txt中
     * */
    public static String writeLog(long startTime,String processID,String sql,long costTime){
//    public static String writeLog(){
        String filePath = "Log.txt";
        String message = Long.toString(startTime)+","+processID+","+sql+","+Long.toString(costTime);

        String res = "";
        FileWriter fw = null;
        PrintWriter pw = null;
        File f = new File(filePath);
        try {
            fw = new FileWriter(f,true);
            pw = new PrintWriter(fw);
            pw.println(message);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("写入txt文件出现异常！");
        }finally {
            try {
                pw.flush();
                fw.flush();
                pw.close();
                fw.close();
                res = "log out successed";
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("在刷新/关闭txt文件出现异常！");
                res = "log out failed";
            }
        }
        //传递给 Kafka，通过Post请求
        String[] str ={String.valueOf(startTime),processID,sql,String.valueOf(costTime)};
        System.out.println(str);
        JSONObject jsonObject = strTojson(str);
        postToKafa(url,jsonObject);

        return res;
    }

    public static String sendDriverMessage(String file){
        StringBuffer sb = new StringBuffer();
        try { // 防止文件建立或读取失败，用catch捕捉错误并打印，也可以throw
            /* 读入TXT文件 */
            String pathname = file; // 绝对路径或相对路径都可以，这里是绝对路径，写入文件时演示相对路径
            File filename = new File(pathname); // 要读取以上路径的input。txt文件
            if (!filename.exists()){
                return null;
            }
            InputStreamReader reader = new InputStreamReader(
                    new FileInputStream(filename)); // 建立一个输入流对象reader
            BufferedReader br = new BufferedReader(reader); // 建立一个对象，它把文件内容转成计算机能读懂的语言
            String line = "";
            while (line != null) {
                sb.append(line);
                line = br.readLine(); // 一次读入一行数据
            }
            OutputStreamWriter osw = new OutputStreamWriter(new FileOutputStream(file), "utf-8");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sb.toString();
    }
    /*edited by honkui, use the properties file to get the
    applicationName, moduleName,processName,instanceNumber, 日志开关

     */
    private static String[] readProperty1(String path) {
        System.out.println("path:"+path);
        ArrayList<String> arr = new ArrayList<>();
        String[] propertiesName = path.split("/");

        String res = propertiesName[propertiesName.length-1].split("\\.")[0];
        ResourceBundle resourceBundle = ResourceBundle.getBundle(res);

        //遍历取值
        Enumeration enumeration = resourceBundle.getKeys();

        while(enumeration.hasMoreElements()) {
            try {
                String result = (String) enumeration.nextElement();
                System.out.println(result);
                String value = resourceBundle.getString(result);
                arr.add(value);
                System.out.println(new String(value.getBytes("iso-8859-1"), "gbk"));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        return (String[]) arr.toArray(new String[0]);
    }
    // add by hongkui here, 转换成json对象，进行传递
    public static JSONObject strTojson(String[] str){
        String[] temp = readProperty1(jdbcPremain.getAgentargs());
        JSONObject jsonObject = new JSONObject();
        JSONObject in_jsonObject = new JSONObject();
        jsonObject.put("topic","test");
//        String[] key = {"timestamp_sql","processID","sql_Content","cost_Time","applicationName","moduleName","processName","instanceNumber"};
        String[] key = {"timestamp_sql","processID","sql_Content","cost_Time"};
        for(int index =0; index<key.length;index++){
            in_jsonObject.put(key[index],str[index]);
        }
        in_jsonObject.put("modulName",temp[0]);
        // 定义日志开关
        logSwitch = temp[1];
        System.out.println("logSwitch"+logSwitch);
        in_jsonObject.put("instanceNumber",temp[2]);
        in_jsonObject.put("processName",temp[3]);
        in_jsonObject.put("applicationName",temp[4]);
        jsonObject.put("message",in_jsonObject.toJSONString());

        return jsonObject;
    }
    //传递 驱动和用户名以及业务代码名，需要后期匹配strToJson传递的字段
    public static JSONObject strTojson1(String[] str){
        JSONObject jsonObject = new JSONObject();
        JSONObject in_jsonObject = new JSONObject();
        jsonObject.put("topic","test");
        //strings 驱动和用户名以及业务代码名
        String[] key = {"start_time","processID","strings"};
        for(int index =0; index<key.length;index++){
            in_jsonObject.put(key[index],str[index]);
        }
        jsonObject.put("message",in_jsonObject.toJSONString());
        return jsonObject;
    }

    public static String postToKafa(String httpUrl, JSONObject jsonObject) {
        HttpURLConnection connection = null;
        InputStream is = null;
        OutputStream os = null;
        BufferedReader br = null;
        StringBuffer buffer = new StringBuffer();
        try {
            URL url = new URL(httpUrl);// 创建连接
            connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setUseCaches(false);
            connection.setInstanceFollowRedirects(true);
            connection.setRequestMethod("POST"); // 设置请求方式
            connection.setRequestProperty("Accept", "application/json"); // 设置接收数据的格式
            connection.setRequestProperty("Content-Type", "application/json"); // 设置发送数据的格式
            connection.connect();
            OutputStreamWriter out = new OutputStreamWriter(connection.getOutputStream(), "UTF-8"); // utf-8编码
            //out.write(jsonObject.toJSONString());
//            out.write(jsonObject.toString());
            out.write(String.valueOf(jsonObject));

            System.out.println("after"+String.valueOf(jsonObject));

            out.flush();
            out.close();

            int code = connection.getResponseCode();

            if (code == 200) {
                System.out.println("200");
                is = connection.getInputStream();
            } else {
                System.out.println("failed");
                is = connection.getErrorStream();
            }

            // 读取响应
            int length = (int) connection.getContentLength();// 获取长度
            if (length != -1) {
                byte[] data = new byte[length];
                byte[] temp = new byte[512];
                int readLen = 0;
                int destPos = 0;
                while ((readLen = is.read(temp)) > 0) {
                    System.arraycopy(temp, 0, data, destPos, readLen);
                    destPos += readLen;
                }
                String result = new String(data, "UTF-8"); // utf-8编码
                return result;
            }
            System.out.println("Here");
            System.out.println(connection.getResponseCode());
            // 请求返回的状态
            if (HttpURLConnection.HTTP_OK == connection.getResponseCode()){
                System.out.println("连接成功");
                // 请求返回的数据
                InputStream in1 = connection.getInputStream();
                try {
                    String readLine=new String();
                    BufferedReader responseReader=new BufferedReader(new InputStreamReader(in1,"UTF-8"));
                    while((readLine=responseReader.readLine())!=null){
                        buffer.append(readLine).append("\n");
                    }
                    responseReader.close();
                    System.out.println(buffer.toString());

                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            } else {
                System.out.println("error++");

            }

        } catch (IOException e) {
            System.out.println("Exception occur when send http post request!");
        }


        return buffer.toString();
    }

    /*
    * 测试使用
    * */
    public static String tests(){

//        System.out.println("look here----------------:"+jdbcPremain.getAgentargs());
//        String[] agentargs = jdbcPremain.getAgentargs().split("&");
//        for(String s:agentargs){
//            System.out.println(s);
//        }
        String startTime = Long.toString(start);
        String costTime = Long.toString(cost);

        String message = startTime+","+processID+","+sql+","+costTime;
        String res = "";
        if(logSwitch.equals("1")){
        FileWriter fw = null;
        PrintWriter pw = null;
        File f = new File(logPath);
        try {
            fw = new FileWriter(f,true);
            pw = new PrintWriter(fw);
            pw.println(message);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("写入txt文件出现异常！");
        }finally {

            try {
                pw.flush();
                fw.flush();
                pw.close();
                fw.close();
                res = "log out successed";
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("在刷新/关闭txt文件出现异常！");
                res = "log out failed";
            }

        }
        }else {
            System.out.println("日志关闭");
        }
        //传递给 Kafka，通过Post请求
//        String[] str ={String.valueOf(start),processID,sql,String.valueOf(cost),agentargs[0],agentargs[1],agentargs[2],agentargs[3]};
        String[] str ={String.valueOf(start),processID,sql,String.valueOf(cost)};
        System.out.println(str);
        JSONObject jsonObject = strTojson(str);
        postToKafa(url,jsonObject);
        return res;
    }
//    public static void info3(String URL,String filePath){
////        setSql(sql);
////        setCostTime(costTime);
////        setFilePath(filePath);
////        setStartTime(startTime);
////        setProcessID(processID);
//        String message = URL;
//        FileWriter fw = null;
//        PrintWriter pw = null;
//        File f = new File(filePath);
//        try {
//            fw = new FileWriter(f,true);
//            pw = new PrintWriter(fw);
//            pw.println(message);
//        } catch (IOException e) {
//            e.printStackTrace();
//            System.out.println("写入txt文件出现异常！");
//        }finally {
//
//            try {
//                pw.flush();
//                fw.flush();
//                pw.close();
//                fw.close();
//            } catch (IOException e) {
//                e.printStackTrace();
//                System.out.println("在刷新/关闭txt文件出现异常！");
//            }
//
//        }
//    }
    /*
    * 将log传输给消息队列
    * */
//    public static void sendLog(Long startTime,String processID,String sql,Long costTime,String filePath){
//
//    }

    // for split string，拆分张磊磊传入的长字符串
    //jdbc:mysql://localhost:3306/test?useSSL=false&serverTimezone=UTC {user=root, password=123456} class src.agent.testdemo
    public static String[] splitString(String tar){
        String[] res = tar.split("\\s+|:|/|\\?|=|,");
        return res;
    }
    public static void main(String[] args){
//        String tar = "jdbc:mysql://localhost:3306/test?useSSL=false&serverTimezone=UTC {user=root, password=123456} class src.agent.testdemo";
//        String[] res = splitString(tar);
//        for(int i=0; i<res.length;i++){
//            System.out.println(i+": "+res[i]);
//        }
//         System.out.println(jdbcPremain.getAgentargs());

    }

}
