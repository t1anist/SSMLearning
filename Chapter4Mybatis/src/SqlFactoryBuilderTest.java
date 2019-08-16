import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import java.io.IOException;
import java.io.InputStream;

public class SqlFactoryBuilderTest {
    SqlSessionFactory sqlSessionFactory = null;

    public SqlSessionFactory getSqlSessionFactory(){
        if(sqlSessionFactory==null){
            String resources = "mybatis-config.xml";
            try{
                InputStream inputStream = Resources.getResourceAsStream(resources);
                sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
            }catch (IOException e){
                e.printStackTrace();
            }
        }
        return sqlSessionFactory;
    }

}
