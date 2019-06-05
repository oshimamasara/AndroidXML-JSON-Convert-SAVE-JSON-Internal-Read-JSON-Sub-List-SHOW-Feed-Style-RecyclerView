/*
 * Copyright (C) 2017 Google, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.oshimamasara.getjson001;

/**
 * The {@link MenuItem} class.
 * <p>Defines the attributes for a restaurant menu item.</p>
 */
class MenuItem {

    //private final String name;
    //private final String description;
    //private final String price;
    //private final String category;
    //private final String imageName;
    private final String title;
    private final String pubDate;
    private final String description;

    public MenuItem(String title,String pubDate, String description) {
        this.title = title;
        this.pubDate=pubDate;
        this.description = description;
        //this.price = price;
        //this.category = category;
        //this.imageName = imageName;
    }
    public String getTitle(){
        return title;
    }

    public String getPubDate(){
        return pubDate;
    }

    public String getDescription(){
        return description;
    }

    //public String getName() {
    //    return name;
    //}
//
    //public String getDescription() {
    //    return description;
    //}
//
    //public String getPrice() {
    //    return price;
    //}
//
    //public String getCategory() {
    //    return category;
    //}
//
    //public String getImageName() {
    //    return imageName;
    //}
}
