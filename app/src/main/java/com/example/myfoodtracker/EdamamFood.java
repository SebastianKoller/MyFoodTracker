package com.example.myfoodtracker;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.HashMap;
import java.util.Map;

public class EdamamFood
{
    @JsonProperty
    String foodId;
    @JsonProperty
    String uri;
    @JsonProperty
    String label;
    @JsonProperty
    String category;
    @JsonProperty
    String categoryLabel;
    @JsonProperty
    String image;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    public EdamamFood(){ }

    public String getFoodId() {
        return foodId;
    }

    public void setFoodId(String foodId) {
        this.foodId = foodId;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getCategoryLabel() {
        return categoryLabel;
    }

    public void setCategoryLabel(String categoryLabel) {
        this.categoryLabel = categoryLabel;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

    @Override
    public String toString()
    {
        return this.label;
    }

//    public class Nutrients{
//        float ENERC_KCAL;
//        float PROCNT;
//        float FAT;
//        float CHOCDF;
//        float FITBTG;
//
//        public Nutrients(float ENERC_KCAL, float PROCNT, float FAT, float CHOCDF, float FITBTG) {
//            this.ENERC_KCAL = ENERC_KCAL;
//            this.PROCNT = PROCNT;
//            this.FAT = FAT;
//            this.CHOCDF = CHOCDF;
//            this.FITBTG = FITBTG;
//        }
//
//        public float getENERC_KCAL() {
//            return ENERC_KCAL;
//        }
//
//        public void setENERC_KCAL(float ENERC_KCAL) {
//            this.ENERC_KCAL = ENERC_KCAL;
//        }
//
//        public float getPROCNT() {
//            return PROCNT;
//        }
//
//        public void setPROCNT(float PROCNT) {
//            this.PROCNT = PROCNT;
//        }
//
//        public float getFAT() {
//            return FAT;
//        }
//
//        public void setFAT(float FAT) {
//            this.FAT = FAT;
//        }
//
//        public float getCHOCDF() {
//            return CHOCDF;
//        }
//
//        public void setCHOCDF(float CHOCDF) {
//            this.CHOCDF = CHOCDF;
//        }
//
//        public float getFITBTG() {
//            return FITBTG;
//        }
//
//        public void setFITBTG(float FITBTG) {
//            this.FITBTG = FITBTG;
//        }
//    }
}
