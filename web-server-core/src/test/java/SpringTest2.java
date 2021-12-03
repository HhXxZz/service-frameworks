import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.hxz.service.frameworks.dao.User;
import org.hxz.service.frameworks.dao.UserMapper;
import org.hxz.service.frameworks.utils.AppContext;
import org.hxz.service.frameworks.utils.GsonUtil;
import org.hxz.service.frameworks.utils.RedisUtil;
import org.hxz.service.frameworks.utils.UUIDUtil;
import org.hxz.service.frameworks.vo.MyBeans;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by hxz on 2021/11/29 10:08.
 */

//@Component
@ComponentScan({"org.hxz"})
@MapperScan("org.hxz.service.frameworks.dao")
@EnableAutoConfiguration
public class SpringTest2 {

    public static void main(String[] args) {
        SpringApplication application = new SpringApplication(SpringTest2.class);
        application.run(args);
//        ConfigurableApplicationContext run = SpringApplication.run(SpringTest2.class, args);
//        /*2.查看容器里面的组件*/
//        String[] names = run.getBeanDefinitionNames();
//        for(String name:names){
//            System.out.println(name);
//        }

        Environment environment = AppContext.getBean(Environment.class);
//        System.out.println(environment.getProperty("spring.port","2121"));
        String nacosModule = environment.getProperty("nacos.module","[\"module\"]");
        System.out.println(nacosModule);
        System.out.println(RedisUtil.set("aa","sosoosos"));

        System.out.println(RedisUtil.get("aa"));

//        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(SpringTest2.class);
//        UserMapper userMapper = context.getBean(UserMapper.class);
//        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
////        queryWrapper.eq(User::getUid,662346002866462721L);
//        queryWrapper.orderByDesc(User::getUid);
//        queryWrapper.last(" limit 100 ");
//
//        List<User> users = userMapper.selectList(queryWrapper);
//        System.out.println(GsonUtil.toJson(users));

//        for(int i=0;i<100;i++) {
//            User user = new User();
//            user.setName(UUIDUtil.randomString(8, false));
//            userMapper.insert(user);
//        }
    }

}
