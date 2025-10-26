package kr.co.yeogiga.common.util;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.StringTokenizer;

public class RegionUtil {
    private static Set<String> state = new HashSet<>(Arrays.asList(new String[]{
            "경기도", "강원도", "충청북도", "충청남도", "경상북도", "경상남도", "전라남도", "전라북도"
    }));
    
    /**
     * 주소지에서 지역(도시)를 추출하는 메서드
     *
     * @param address   주소
     * @return          지역(도시)
     */
    public static String extractRegion(String address) {
        StringTokenizer st = new StringTokenizer(address);
        
        while(st.hasMoreTokens()) {
            String token = st.nextToken();
            if (state.contains(token)) {
                continue;
            }
            return token;
        }
        
        return address.substring(0, address.indexOf(' ') + 1);
    }
}
