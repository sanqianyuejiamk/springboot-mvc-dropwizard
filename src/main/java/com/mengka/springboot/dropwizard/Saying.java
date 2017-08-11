package com.mengka.springboot.dropwizard;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.validator.constraints.Length;

/**
 * 新建Representation类
 * <p>
 * 》》通过下面的json来调用hello world：
 * {
 * "id": 1,
 * "content": "Hi!"
 * }
 *
 * @author mengka
 * @date 2017/08/11.
 */
public class Saying {

    private long id;

    @Length(max = 3)
    private String content;

    public Saying() {
        // Jackson deserialization
    }

    public Saying(long id, String content) {
        this.id = id;
        this.content = content;
    }

    @JsonProperty
    public long getId() {
        return id;
    }

    @JsonProperty
    public String getContent() {
        return content;
    }
}
