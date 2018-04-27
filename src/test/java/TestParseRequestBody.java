import com.light.util.BytesUtil;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.Arrays;
import java.util.List;

/**
 * Created on 2018/4/26.
 */
@RunWith(JUnit4.class)
public class TestParseRequestBody {

    @Test
    public void test(){
        Assert.assertFalse(1 == 2);
    }

    @Test
    public void test2(){
        byte[] bytes = new String("name=\"qqq\"; filename=\"public.txt\"").getBytes();
        List<Integer> indexes = BytesUtil.findAll(bytes,"\"");
        indexes.forEach(System.out::println);
        System.out.println(new String(Arrays.copyOfRange(bytes, indexes.get(0) + 1, indexes.get(1))));
        System.out.println(new String(Arrays.copyOfRange(bytes, indexes.get(2) + 1, indexes.get(3))));

        Assert.assertTrue(indexes.get(0).equals(5));
        Assert.assertTrue(indexes.get(1).equals(9));
        Assert.assertTrue(indexes.get(2).equals(21));
        Assert.assertTrue(indexes.get(3).equals(32));
    }
}
