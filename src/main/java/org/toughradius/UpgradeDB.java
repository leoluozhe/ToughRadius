package org.toughradius;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import org.toughradius.common.FileUtil;

public class UpgradeDB {

    
    static{
         try {
            Class.forName("org.hsqldb.jdbc.JDBCDriver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } 
    }
    
    
    public  String  readIn() throws IOException{
        System.out.print(">> ");
        return new BufferedReader(new InputStreamReader(System.in)).readLine();
    }
    
    public  Connection getConnection(String dbname) throws SQLException{
        

        Connection conn  = DriverManager.getConnection("jdbc:hsqldb:./data/"
                + dbname,
                "SA", 
                "");  
        conn.setAutoCommit(false);
        
        return conn;
    }
    
    public void createTales(Connection conn) throws SQLException
    {
        Statement st = conn.createStatement();
        
        System.out.println("正在创建表");

        String script = FileUtil.readFile("./docs/create.sql", "GBK");
        script = script.replaceAll("--.*", "");
        String[] sqls = script.split(";");
        
        for (String sql : sqls) {
            if("".equals(sql.trim()))
                continue;
            System.out.println(sql.trim());
            try {
                st.execute(sql.trim());
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("创建表时发生错误："+e+sql);
            }
            conn.commit();
        }
        st.close();
    }
    


    
    public void importSql(Connection conn, File sqlfile) throws SQLException {
        System.out.println("正在导入外部sql:" + sqlfile.getAbsolutePath());

        Statement st = conn.createStatement();
        String sql = FileUtil.readFile(sqlfile.getPath(), "GBK");
        if(!"".equals(sql))
        {
            st.execute(sql);
            conn.commit();
        }
        
    }

    
    public void shutdown(Connection conn) throws SQLException
    {
        System.out.println("正在shutdown");
        Statement st = conn.createStatement();
        st.execute("SHUTDOWN SCRIPT");
        conn.close();
    }
    

    public static void main(String[] args) throws Exception {
        String dbname = null;
        String importSql = null;
        String deleteOld = null;
        if(args.length>=3)
        {
            dbname = args[0];
            deleteOld = args[1];
            importSql = args[2];
        }
        
        UpgradeDB install = new UpgradeDB();
        
        if(dbname == null){
            System.out.println("即将初始化数据库,请输入数据库名");
            dbname = install.readIn();
        }
            
        if("".equals(dbname)){
            System.out.println("数据库名不能为空");
            return;
        }

        if(FileUtil.isExists(String.format("./data/%s.properties", dbname)))
        {
            if(deleteOld==null)
            {
                System.out.println("数据库已经存在，是否删除： y/n");
                deleteOld = install.readIn();
            }

            if(deleteOld.equalsIgnoreCase("y"))
            {
                FileUtil.deleteFile(String.format("./data/%s.properties", dbname));
                FileUtil.deleteFile(String.format("./data/%s.script", dbname));
                FileUtil.deleteFile(String.format("./data/%s.log", dbname));
                FileUtil.deleteFile(String.format("./data/%s.loc", dbname));
            }
            
        }
        

        
        Connection conn = install.getConnection(dbname);
        install.createTales(conn);

        if(importSql==null)
        {
            System.out.println("是否导入外部sql： y/n");
            importSql = install.readIn();

        }
        if(importSql.equalsIgnoreCase("y"))
        {
            File sqldir = new File("./data/import");
            File[] files = sqldir.listFiles();
            
            for (File sfile : files) {
                if(sfile.getPath().endsWith("sql")){
                    try {
                        install.importSql(conn,sfile);
                    } catch (Exception e) {
                        System.out.println("导入sql失败："+sfile.getPath()+e);
                    }
                    
                }
            }
        }

        install.shutdown(conn);
        
        
        
    }

}
