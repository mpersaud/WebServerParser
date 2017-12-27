package com.mike;


import java.io.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Main {

    public static void main(String[] args) {
	// write your code here
        if(args.length!=3){
            System.out.println("Invalid amount of Args");
            return;
        }
        String startDate=args[0];
        String duration= args[1];
        String threshold= args[2];
        List<LogItem> itemList = new ArrayList<>();
        try {
            BufferedReader br = new BufferedReader(new FileReader(new File("access.log")));
            String line;

            while((line =br.readLine())!=null){

                String [] tokens = line.split("\\|");
                String date = tokens[0];
                String ipAddress= tokens[1];
                String request = tokens[2];
                String status= tokens[3];
                String userAgent= tokens[4];
                LogItem item = new LogItem(date,ipAddress,request,Integer.parseInt(status),userAgent);
                itemList.add(item);

            }
            br.close();
        }catch (FileNotFoundException fe){
            fe.printStackTrace();
        }catch (IOException ioe){

        }

        Database database = new Database();
        database.getConnection();
        database.createLogTable(itemList);
        database.runQuery(startDate,duration,threshold);

    }
    public void checkRequest(){

    }
}
