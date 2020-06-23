package com.jstarcraft.core.storage.elasticsearch;

import java.util.Objects;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Document(indexName = "mock")
public class Mock {

    @Id
    private Long id;

    /** 标题 */
    @Field(type = FieldType.Text)
    private String title;

    /** 类目 */
    @Field(type = FieldType.Keyword)
    private String category;

    /** 价格 */
    @Field(type = FieldType.Double)
    private Double price;

    Mock() {
    }

    public Mock(Long id, String title, String category, Double price) {
        this.id = id;
        this.title = title;
        this.category = category;
        this.price = price;
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getCategory() {
        return category;
    }

    public Double getPrice() {
        return price;
    }

    @Override
    public int hashCode() {
        return Objects.hash(category, id, price, title);
    }

    @Override
    public boolean equals(Object object) {
        if (this == object)
            return true;
        if (object == null)
            return false;
        if (getClass() != object.getClass())
            return false;
        Mock that = (Mock) object;
        return Objects.equals(this.category, that.category) && Objects.equals(this.id, that.id) && Objects.equals(this.price, that.price) && Objects.equals(this.title, that.title);
    }

}
