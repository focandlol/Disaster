package focandlol.disaster.service;

import focandlol.disaster.domain.es.Region;
import java.util.HashSet;
import java.util.Set;
import org.springframework.stereotype.Component;

@Component
public class MessageUtil {

  public Set<Region> getRegionSet(String region) {
    Set<Region> regionSet = new HashSet<>();

    String[] regions = region.split(",");

    for (String r : regions) {
      r = r.trim();
      if (r.isEmpty()) continue;

      String[] parts = r.split("\\s+");

      String sido = "시도 정보 없음";
      String sigungu = "전체";

      if (parts.length >= 1) {
        String p0 = parts[0];

        if (p0.endsWith("도") || p0.endsWith("특별시") || p0.endsWith("광역시") || p0.endsWith("자치시")) {
          sido = p0;

          if (parts.length >= 2) {
            String p1 = parts[1];
            if (p1.endsWith("시") || p1.endsWith("군") || p1.endsWith("구")) {
              sigungu = p1;
            }
          }
        } else {
          // p0이 시군구일 경우
          sigungu = p0;
        }
      }
      regionSet.add(new Region(sido, sigungu));
    }

    return regionSet;
  }
}
