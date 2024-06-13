package com.hanqf.ai_agent.tools;

import com.hanqf.ai_agent.service.Assistant;
import dev.langchain4j.agent.tool.Tool;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

/**
 * <h1></h1>
 * Created by hanqf on 2024/6/13 14:44.
 */


@Component
public class AssistantTools {
    RestTemplate restTemplate = new RestTemplate();

    public enum Unit {C, F}

    public record Request(String location, Unit unit) {
    }

    public record Response(double temp, Unit unit) {
    }

    /**
     * This tool is available to {@link Assistant}
     */
    @Tool
    Response weather(Request request) {
        return new Response(Math.random() * 40, Unit.C);
    }

    /**
     * 根据POI名称和所在城市，获取POI的经纬度坐标。
     *
     * @param location POI名称，必须是中文。
     * @param city     POI所在的城市名，必须是中文。
     * @return 返回一个表示经纬度坐标的对象，例如：{ "longitude": "120.7589", "latitude": "31.2172" }。
     * @throws IllegalArgumentException 如果location或city参数为空，则抛出此异常。
     */

    @Value("${amap.key}")
    private String amapKey;

    @Tool
    public Map<String, Object> getLocationCoordinate2(String location, String city) {

        String url = String.format("https://restapi.amap.com/v5/place/text?key=%s&keywords=%s&region=%s",
                amapKey, location, city);

        ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);

        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
            Map<String, Object> result = response.getBody();
            if (result.containsKey("pois") && !((List)result.get("pois")).isEmpty()) {
               return  (Map<String, Object>)((List)result.get("pois")).get(0);
            }
        }

        return null;
    }

    @Tool
    public String searchNearbyPois(String location, String keyword) {
        String url = String.format("https://restapi.amap.com/v5/place/around?key=%s&keywords=%s&location=%s",
                amapKey, keyword, location);

        ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);

        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
            Map<String, Object> result = response.getBody();
            if (result.containsKey("pois") && result.get("pois") instanceof List) {
                List<Map<String, Object>> pois = (List<Map<String, Object>>) result.get("pois");

                StringBuilder ans = new StringBuilder();
                for (int i = 0; i < Math.min(3, pois.size()); i++) {
                    Map<String, Object> poi = pois.get(i);
                    String name = (String) poi.getOrDefault("name", "");
                    String address = (String) poi.getOrDefault("address", "");
                    String distance = (String) poi.getOrDefault("distance", "");

                    ans.append(name).append("\n")
                            .append(address).append("\n")
                            .append("距离：").append(distance).append("米\n\n");
                }

                return ans.toString();
            }
        }

        return "";
    }


}
