package org.example;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

/*
* 负责输出日志，尝试用java原生环境的方法
* */
public class MonitorLog {

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
        return res;
    }
    /*
    * 测试使用
    * */
//    public static String tests(Long startTime,Long costTime,String filePath){
////        setSql(sql);
////        setCostTime(costTime);
////        setFilePath(filePath);
////        setStartTime(startTime);
////        setProcessID(processID);
//        String message = Long.toString(startTime)+Long.toString(costTime);
//        String res = "";
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
//                res = "log out successed";
//            } catch (IOException e) {
//                e.printStackTrace();
//                System.out.println("在刷新/关闭txt文件出现异常！");
//                res = "log out failed";
//            }
//
//        }
//        return res;
//    }
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

}
