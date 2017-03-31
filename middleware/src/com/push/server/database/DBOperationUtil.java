package com.push.server.database;

import java.io.IOException;
import java.io.Reader;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

public class DBOperationUtil {
	
	private static final String RESOURCE = "MybatisConfig.xml";
    private static SqlSessionFactory sqlSessionFactory = null;
    private static ThreadLocal<SqlSession> threadLocal = new ThreadLocal<SqlSession>();
 
    static {
        Reader reader = null;
        try {
            reader = Resources.getResourceAsReader(RESOURCE);
        } catch (IOException e) {
            throw new RuntimeException("Get resource error:"+RESOURCE, e);
        }
 
        sqlSessionFactory = new SqlSessionFactoryBuilder().build(reader);
    }
     
    /**
     * Function  : 获得SqlSessionFactory
     */
    public static SqlSessionFactory getSqlSessionFactory(){  
        return sqlSessionFactory;  
    }
     
    /**
     * Function  : 重新创建SqlSessionFactory
     */
    public static void rebuildSqlSessionFactory(){
        Reader reader = null;
        try {
            reader = Resources.getResourceAsReader(RESOURCE);
        } catch (IOException e) {
            throw new RuntimeException("Get resource error:"+RESOURCE, e);
        }
 
        sqlSessionFactory = new SqlSessionFactoryBuilder().build(reader);
    }
     
    /**
     * Function  : 获取sqlSession
     */
    public static SqlSession getSession(){
        SqlSession session = threadLocal.get();
         
        if(session!=null){
            if(sqlSessionFactory == null){
                getSqlSessionFactory();
            }
            //如果sqlSessionFactory不为空则获取sqlSession，否则返回null
            session = (sqlSessionFactory!=null) ? sqlSessionFactory.openSession(): null;
        }
         
        return session;
    }
     
    /**
     * Function  : 关闭sqlSession
     */
    public static void closeSession(){
        SqlSession session = threadLocal.get();
        threadLocal.set(null);
        if(session!=null){
            session.close();
        }
    }
}
