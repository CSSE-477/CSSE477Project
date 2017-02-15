package protocol;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

public class HttpPriorityElementComparator implements Comparator<HttpPriorityElement> {
	private Map<String, Integer> valueMap;

	public HttpPriorityElementComparator() {
        this.valueMap = new HashMap<>();
        this.valueMap.put(Protocol.getProtocol().getStringRep(Keywords.GET), 2);
        this.valueMap.put(Protocol.getProtocol().getStringRep(Keywords.DELETE), 3);
        this.valueMap.put(Protocol.getProtocol().getStringRep(Keywords.HEAD), 1);
        this.valueMap.put(Protocol.getProtocol().getStringRep(Keywords.POST), 4);
        this.valueMap.put(Protocol.getProtocol().getStringRep(Keywords.PUT), 5);
	}

    @Override
    public int compare(HttpPriorityElement o1, HttpPriorityElement o2) {
        HttpRequest o1Req = o1.getRequest();
        HttpRequest o2Req = o2.getRequest();
        LocalDateTime now = LocalDateTime.now();
        if(o1.getTime().minusSeconds(1).isAfter(now) && !o2.getTime().minusSeconds(1).isAfter(now)) {
            return -1;
        }
        int o1Total = getMethodVal(o1Req.getMethod());
        int o2Total = getMethodVal(o2Req.getMethod());
        int o1Length;
        try{
            o1Length = Integer.parseInt(o1Req.getHeader()
                    .get(Protocol.getProtocol().getStringRep(Keywords.CONTENT_LENGTH)));
        } catch (Exception e){
            o1Length = 0;
        }
        int o2Length;
        try{
            o2Length = Integer.parseInt(o2Req.getHeader()
                    .get(Protocol.getProtocol().getStringRep(Keywords.CONTENT_LENGTH)));
        } catch (Exception e){
            o2Length = 0;
        }
        o2Length += 1;
        o1Length += 1;
        o1Total = o1Total * getPayloadSizeFactor(o1Req.getMethod(), o1Length);
        o2Total = o2Total * getPayloadSizeFactor(o2Req.getMethod(), o2Length);
        if(o1Total < o2Total) {
            return -1;
        }
        if(o1Total > o2Total) {
            return 1;
        }
        return 0;
    }

    int getMethodVal(String method){
        if(method == null){
            return 1;
        }
        Integer value = valueMap.get(method);
        if(value == null){
            return 1;
        }
        return value;
    }

    int getPayloadSizeFactor(String method, int payloadSize){
        if(method == null) {
            return 1;
        }
        if(method.equals(Protocol.getProtocol().getStringRep(Keywords.POST)) || method.equals(Protocol.getProtocol().getStringRep(Keywords.PUT))){
            return payloadSize;
        }
        return 1;
    }
}