package mob.code.supermarket;

import mob.code.supermarket.bean.Item;
import mob.code.supermarket.model.Promotion;
import mob.code.supermarket.repository.ItemRepository;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.alibaba.fastjson.JSON;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = Application.class)
@RunWith(SpringRunner.class)
public class SupermarketControllerTest {

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private ItemRepository itemRepository;

    @Before
    public void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        itemRepository.save(new Item("12345678", "pizza", "", 15.00, "0", Promotion.TWO_FOR_ONE));
        itemRepository.save(new Item("22345678", "milk", "L", 12.30, "1", Promotion.FIVE_PERCENT_OFF));
        itemRepository.save(new Item("33456789", "apple", "KG", 9.90, "1", Promotion.NONE));

    }

    @After
    public void tearDown() {
        itemRepository.deleteAll();
    }

    @Test
    public void test_ping() throws Exception {
        mockMvc.perform(get("/ping"))
                .andExpect(status().isOk());
    }

    @Test
    public void API应该返回指定的JSON对象() throws Exception {
        String contentAsString = accessApi(Arrays.asList("12345678", "22345678-3"));
        String json = "{\n" +
                "  \"data\": [\n" +
                "    \"****** SuperMarket receipt ******\",\n" +
                "    \"pizza: 1 x 15.00 --- 15.00\",\n" +
                "    \"milk: 3(L) x 12.30 --- 36.90 (-1.84)\",\n" +
                "    \"---------------------------------\",\n" +
                "    \"total: 51.90(CNY)\",\n" +
                "    \"      -1.84(CNY)\",\n" +
                "    \"*********************************\"\n" +
                "  ]\n" +
                "}";
        JSONAssert.assertEquals(json, contentAsString, false);
    }

    private String accessApi(List<String> strings) throws Exception {
        return mockMvc.perform(post("/scan")
                .content(JSON.toJSONString(strings))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
    }

    @Test
    public void 如果数据不存在_则返回对应的错误信息() throws Exception {
        String contentAsString = accessApi(Collections.singletonList("123456789"));
        String json = "{" +
                "\"data\":null," +
                "\"error\":\"item doesn't exist: 123456789\"" +
                "}";
        JSONAssert.assertEquals(json, contentAsString, false);
    }

    @Test
    public void 重复扫码的商品应该汇总() throws Exception {
        String contentAsString = accessApi(Arrays.asList("12345678", "12345678", "12345678"));
        String json = "{\n" +
                "  \"data\": [\n" +
                "    \"****** SuperMarket receipt ******\",\n" +
                "    \"pizza: 3 x 15.00 --- 45.00\",\n" +
                "    \"---------------------------------\",\n" +
                "    \"Two-for-one:\",\n" +
                "    \"pizza: 1\",\n" +
                "    \"---------------------------------\",\n" +
                "    \"total: 45.00(CNY)\",\n" +
                "    \"      -15.00(CNY)\",\n" +
                "    \"*********************************\"\n" +
                "  ]\n" +
                "}";
        JSONAssert.assertEquals(json, contentAsString, false);
    }

    @Test
    public void 称重商品应该支持小数() throws Exception {
        String contentAsString = accessApi(Collections.singletonList("22345678-1.5"));
        String json = "{\n" +
                "  \"data\": [\n" +
                "    \"****** SuperMarket receipt ******\",\n" +
                "    \"milk: 1.5(L) x 12.30 --- 18.45 (-0.92)\",\n" +
                "    \"---------------------------------\",\n" +
                "    \"total: 18.45(CNY)\",\n" +
                "    \"      -0.92(CNY)\",\n" +
                "    \"*********************************\"\n" +
                "  ]\n" +
                "}";
        JSONAssert.assertEquals(json, contentAsString, false);
    }

    @Test
    public void 称重商品数量不支持1位以上的小数() throws Exception {
        String contentAsString = accessApi(Collections.singletonList("22345678-1.55"));
        String json = "{\n" +
                "\"data\": null,\n" +
                "\"error\": \"wrong quantity of 22345678\"\n" +
                "}";
        JSONAssert.assertEquals(json, contentAsString, false);
    }

    @Test
    public void 支持原始条码信息() throws Exception {
        String[] rawBarcode = {
                "    _  _     _  _  _  _ ",
                "|   _| _||_||_ |_   ||_|",
                "|  |_  _|  | _||_|  ||_|",
                "",
                " _  _  _     _  _  _  _ ",
                " _| _| _||_||_ |_   ||_|",
                "|_ |_  _|  | _||_|  ||_|",
                "3.5"
        };
        String contentAsString = accessApi(Arrays.asList(rawBarcode));
        String json = "{\n" +
                "  \"data\": [\n" +
                "    \"****** SuperMarket receipt ******\",\n" +
                "    \"pizza: 1 x 15.00 --- 15.00\",\n" +
                "    \"milk: 3.5(L) x 12.30 --- 43.05 (-2.15)\",\n" +
                "    \"---------------------------------\",\n" +
                "    \"total: 58.05(CNY)\",\n" +
                "    \"      -2.15(CNY)\",\n" +
                "    \"*********************************\"\n" +
                "  ]\n" +
                "}";
        JSONAssert.assertEquals(json, contentAsString, false);
    }

    @Test
    public void 如果无法识别应该返回错误信息() throws Exception {
        String[] rawBarcode = {
                "    _  _     _  _  _  _ ",
                "11222222222331",
                "|  |_  _|  | _||_|  ||_|",
                "",
                " _  _  _     _  _  _  _ ",
                " _| _| 22331   ||_|",
                "|_ |_  _|  | _||_|  ||_|",
                "3.5"
        };
        String contentAsString = accessApi(Arrays.asList(rawBarcode));
        String json = "{\n" +
                "    \"data\": null,\n" +
                "    \"error\": \"can not recognize barcode:\\n    _  _     _  _  _  _ \\n11222222222331\\n|  |_  _|  | _||_|  ||_|\\n\\n _  _  _     _  _  _  _ \\n _| _| 22331   ||_|\\n|_ |_  _|  | _||_|  ||_|\\n3.5\"\n" +
                "}";
        JSONAssert.assertEquals(json, contentAsString, false);
    }

    @Test
    public void 称重商品数量不能为0() throws Exception {
        String contentAsString = accessApi(Collections.singletonList("22345678-0"));
        String json = "{\n" +
                "\"data\": null,\n" +
                "\"error\": \"wrong quantity of 22345678\"\n" +
                "}";
        JSONAssert.assertEquals(json, contentAsString, false);
    }

    @Test
    public void 商品数量不能为0() throws Exception {
        String contentAsString = accessApi(Collections.singletonList("12345678-0"));
        String json = "{\n" +
                "\"data\": null,\n" +
                "\"error\": \"wrong quantity of 12345678\"\n" +
                "}";
        JSONAssert.assertEquals(json, contentAsString, false);
    }

    @Test
    public void 买三个参加买二送一促销活动的商品总价会减去一个商品的钱() throws Exception {
        String contentAsString = accessApi(Arrays.asList("12345678-4", "22345678-3"));
        String json = "{\n" +
                "  \"data\": [\n" +
                "    \"****** SuperMarket receipt ******\",\n" +
                "    \"pizza: 4 x 15.00 --- 60.00\",\n" +
                "    \"milk: 3(L) x 12.30 --- 36.90 (-1.84)\",\n" +
                "    \"---------------------------------\",\n" +
                "    \"Two-for-one:\",\n" +
                "    \"pizza: 1\",\n" +
                "    \"---------------------------------\",\n" +
                "    \"total: 96.90(CNY)\",\n" +
                "    \"      -15.00(CNY)\",\n" +
                "    \"      -1.84(CNY)\",\n" +
                "    \"*********************************\"\n" +
                "  ]\n" +
                "}";
        JSONAssert.assertEquals(json, contentAsString, false);
    }


    @Test
    public void 如果是九五折商品则按九五折计算价格() throws Exception {
        String contentAsString = accessApi(Arrays.asList("12345678-4", "22345678-3"));
        String json = "{\n" +
                "  \"data\": [\n" +
                "    \"****** SuperMarket receipt ******\",\n" +
                "    \"pizza: 4 x 15.00 --- 60.00\",\n" +
                "    \"milk: 3(L) x 12.30 --- 36.90 (-1.84)\",\n" +
                "    \"---------------------------------\",\n" +
                "    \"Two-for-one:\",\n" +
                "    \"pizza: 1\",\n" +
                "    \"---------------------------------\",\n" +
                "    \"total: 96.90(CNY)\",\n" +
                "    \"      -15.00(CNY)\",\n" +
                "    \"      -1.84(CNY)\",\n" +
                "    \"*********************************\"\n" +
                "  ]\n" +
                "}";
        JSONAssert.assertEquals(json,contentAsString,false);
    }


    @Test
    public void 重复扫码的商品应该汇总且满足促销活动的商品也会对应减去正确的金额并生成正确的小票() throws Exception {
        String contentAsString = accessApi(Collections.singletonList("12345678-6"));
        String json = "{\n" +
                "  \"data\": [\n" +
                "    \"****** SuperMarket receipt ******\",\n" +
                "    \"pizza: 6 x 15.00 --- 90.00\",\n" +
                "    \"---------------------------------\",\n" +
                "    \"Two-for-one:\",\n" +
                "    \"pizza: 2\",\n" +
                "    \"---------------------------------\",\n" +
                "    \"total: 90.00(CNY)\",\n" +
                "    \"      -30.00(CNY)\",\n" +
                "    \"*********************************\"\n" +
                "  ]\n" +
                "}";
        JSONAssert.assertEquals(json, contentAsString, false);
    }

    @Test
    public void 不参加促销活动的商品则生成无促销条目的正常小票() throws Exception {
        String contentAsString = accessApi(Collections.singletonList("33456789-2.5"));
        String json = "{\n" +
                "  \"data\": [\n" +
                "    \"****** SuperMarket receipt ******\",\n" +
                "    \"apple: 2.5(KG) x 9.90 --- 24.75\",\n" +
                "    \"---------------------------------\",\n" +
                "    \"total: 24.75(CNY)\",\n" +
                "    \"*********************************\"\n" +
                "  ]\n" +
                "}";
        JSONAssert.assertEquals(json, contentAsString, false);
    }

    @Test
    public void 多种商品混合购买也应该生成正确的小票() throws Exception {
        String contentAsString = accessApi(Arrays.asList("33456789-2.5","12345678-4","22345678-3.5"));
        String json = "{\n" +
                "  \"data\": [\n" +
                "    \"****** SuperMarket receipt ******\",\n" +
                "    \"apple: 2.5(KG) x 9.90 --- 24.75\",\n" +
                "    \"pizza: 4 x 15.00 --- 60.00\",\n" +
                "    \"milk: 3.5(L) x 12.30 --- 43.05 (-2.15)\",\n" +
                "    \"---------------------------------\",\n" +
                "    \"Two-for-one:\",\n" +
                "    \"pizza: 1\",\n" +
                "    \"---------------------------------\",\n" +
                "    \"total: 127.80(CNY)\",\n" +
                "    \"      -15.00(CNY)\",\n" +
                "    \"      -2.15(CNY)\",\n" +
                "    \"*********************************\"\n" +
                "  ]\n" +
                "}";
        JSONAssert.assertEquals(json, contentAsString, false);
    }
}
