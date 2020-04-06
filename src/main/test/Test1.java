import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author huangyongwen
 * @date 2020/3/12
 * @Description
 */
public class Test1 {
    @Test
    public void p(){
        int i=1;
        System.out.println(++i);
        System.out.println(i++);
        double j = Math.random();
        System.out.println(j);
        Map<Integer, Integer> imageIdMap = new HashMap<>();

        imageIdMap.put(1,1);
        System.out.println(imageIdMap.put(2,3));
        System.out.println(imageIdMap.put(2,3));

        List list = new ArrayList();
        System.out.println(list.add(1));
        System.out.println(list.add(1));

    }
}
