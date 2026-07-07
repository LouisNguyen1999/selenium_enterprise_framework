package data;

import utils.JsonUtils;

import java.util.Map;

public class TestDataLoader {
    public Map<String, Object> loadLoginData() {
        return JsonUtils.read("testdata/login.json");
    }

    public Map<String, Object> loadProducts() {
        return JsonUtils.read("testdata/products.json");
    }
}
