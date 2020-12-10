package analyzer.app.dto;

import java.io.Serializable;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * DTO 부모 클래스. serialize/deserialize 제공을 위해 구현함
 *
 */
public class CommonDto implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 자신의 객체를 Json String 으로 Serialize 한다.
	 * 
	 * @return
	 * @throws JsonProcessingException
	 */
	public String serialize() throws JsonProcessingException {
		ObjectMapper objectMapper = new ObjectMapper();
		return objectMapper.writeValueAsString(this);
	}
	
	@SuppressWarnings("unchecked")
	public <T> T deSerialize(String json) throws JsonMappingException, JsonProcessingException {
		ObjectMapper objectMapper = new ObjectMapper();
		return (T) objectMapper.readValue(json, this.getClass());
	}

	@Override
	public String toString() {
		return super.toString();
	}
	
	
}
