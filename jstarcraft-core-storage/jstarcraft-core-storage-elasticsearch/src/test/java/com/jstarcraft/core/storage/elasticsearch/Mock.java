package com.jstarcraft.core.storage.elasticsearch;

import java.util.Arrays;
import java.util.Objects;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Document(indexName = "mock")
@TypeAlias("Mock")
public class Mock {

    @Id
    @Field(type = FieldType.Long)
    private Long id;

    /** 标题 */
    @Field(type = FieldType.Text, analyzer = "whitespace", searchAnalyzer = "whitespace")
    private String title;

    /** 类目 */
    @Field(type = FieldType.Keyword)
    private String[] categories;

    /** 价格 */
    @Field(type = FieldType.Double)
    private Double price;

    @Field(type = FieldType.Keyword)
    private MockEnumeration race;

    Mock() {
    }

    public Mock(Long id, String title, String[] categories, Double price) {
        this.id = id;
        this.title = title;
        this.categories = categories;
        this.price = price;
        this.race = MockEnumeration.RANDOM;
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String[] getCategories() {
        return categories;
    }

    public Double getPrice() {
        return price;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, price, title, categories);
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
        return Objects.equals(this.id, that.id) && Objects.equals(this.price, that.price) && Objects.equals(this.title, that.title) && Arrays.equals(this.categories, that.categories);
    }

}
