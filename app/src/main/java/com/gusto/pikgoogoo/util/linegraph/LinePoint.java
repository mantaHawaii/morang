package com.gusto.pikgoogoo.util.linegraph;

/*
 * 	   Created by Daniel Nadeau
 * 	   daniel.nadeau01@gmail.com
 * 	   danielnadeau.blogspot.com
 *
 *
 * 	   Licensed to the Apache Software Foundation (ASF) under one
       or more contributor license agreements.  See the NOTICE file
       distributed with this work for additional information
       regarding copyright ownership.  The ASF licenses this file
       to you under the Apache License, Version 2.0 (the
       "License"); you may not use this file except in compliance
       with the License.  You may obtain a copy of the License at

         http://www.apache.org/licenses/LICENSE-2.0

       Unless required by applicable law or agreed to in writing,
       software distributed under the License is distributed on an
       "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
       KIND, either express or implied.  See the License for the
       specific language governing permissions and limitations
       under the License.
 */

import android.graphics.Path;
import android.graphics.Region;

public class LinePoint {

    private float x = 0;
    private float y = 0;
    private int realX = 0;
    private int realY = 0;
    private Path path;
    private Region region;
    private String label_string;
    public LinePoint(float x, float y) {
        super();
        this.x = x;
        this.y = y;
    }

    public LinePoint() { }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public int getRealX() {
        return realX;
    }

    public void setRealX(int realX) {
        this.realX = realX;
    }

    public int getRealY() {
        return realY;
    }

    public void setRealY(int realY) {
        this.realY = realY;
    }

    public Region getRegion() {
        return region;
    }

    public void setRegion(Region region) {
        this.region = region;
    }

    public Path getPath() {
        return path;
    }

    public void setPath(Path path) {
        this.path = path;
    }

    public String getLabel_string() {
        return label_string;
    }

    public void setLabel_string(String label_string) {
        this.label_string = label_string;
    }

}