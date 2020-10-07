package com.javaref.springboot04.dao;

import com.javaref.springboot04.domain.City;
import org.springframework.stereotype.Repository;

import java.util.*;

// @Repository @Controller @Service都并入了@Component，@Component在不指定生命周期时，都是单例的
// 因此任何代码都要保证线程安全
@Repository
public class CityDAO {
    // 先用SynchonizedMap来模拟DAO访问数据库获取数据、并保证线程安全（这里只是演示，暂不考虑互斥带来性能开销）
    static Map<Integer, City> dataMap = Collections.synchronizedMap(new HashMap<Integer, City>());

    public List<City> findAll() {
        return new ArrayList<>(dataMap.values());
    }

    public void save(City city) throws Exception {
        City data = dataMap.get(city.getId());
        if (null != data) {
            throw new Exception("数据已存在");
        } else {
            dataMap.put(city.getId(), city);
        }
    }
}
