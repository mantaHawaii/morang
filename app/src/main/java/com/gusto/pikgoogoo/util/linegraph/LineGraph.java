package com.gusto.pikgoogoo.util.linegraph;

/*
 * 	   Created by Daniel Nadeau
 * 	   daniel.nadeau01@gmail.com
 * 	   danielnadeau.blogspot.com
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

import android.content.Context;
import android.graphics.*;
import android.graphics.Bitmap.Config;
import android.graphics.Paint.Align;
import android.graphics.Path.Direction;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.gusto.pikgoogoo.adapter.VoteHistoryAdapter;
import com.gusto.pikgoogoo.data.GraphDTO;
import com.gusto.pikgoogoo.util.NumberFormatEditor;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

public class LineGraph extends View implements Observer {

    private ArrayList<Line> lines = new ArrayList<Line>();
    private Paint paint = new Paint();
    private Paint txtPaint = new Paint();
    private float minY = 0, minX = 0;
    private float maxY = 0, maxX = 0;
    private boolean isMaxYUserSet = false;
    private int lineToFill = -1;
    private int indexSelected = -1;
    private int indexSelected2 = -1;
    private OnPointClickedListener listener;
    private Bitmap fullImage;
    private boolean shouldUpdate = false;
    private boolean showMinAndMax = false;
    private boolean showHorizontalGrid = false;
    private int gridColor = 0xffffffff;
    private int labelSize = 10;
    private float clickRange = 30;
    private float pointSize = 10;
    private float strokeWidth = 7;
    private int pointColor = Color.GRAY;
    private float bottomPadding = 10;
    private float topPadding = 10;
    private float sidePadding = 10;
    private int bottomLineColor = Color.BLACK;
    private float bottomLineStrokeWidth = 10;
    private boolean minYZero = false;
    private float marginBottom = 0;
    private boolean bottomTextBold = false;
    private int bottomTextColor = 0;
    private float zeroMarginMultiflier = 1.5f;
    private float labelSidePadding = 10;
    private float labelBottomPadding = 10;
    private float guideStrokeWidth = 5;
    private int guideStrokeColor = Color.GRAY;
    private float guideMargin = 10;
    private boolean showBottomLine = true;
    private int pointTextColor = 0;
    private int textBoxPadding = 10;
    private int textBoxColor = Color.BLACK;
    private int textBoxTextColor = Color.WHITE;
    private int maxTextBoxColor = Color.BLACK;
    private float realMaxY = 0;
    private float realMinY = 0;
    private int textBoxMaxHeight = 0;
    private boolean notClicked = true;
    private int startPadding = 10;
    private int endPadding = 10;
    private float boxTextSize = 10;
    private boolean hasShownMax = false;
    private float guideTextSize = 10;
    private int guideTextColor = Color.BLACK;
    private String startDateStr = "";
    private String endDateStr = "";

    public LineGraph(Context context){
        this(context,null);
    }
    public LineGraph(Context context, AttributeSet attrs) {
        super(context, attrs);
        txtPaint.setColor(0xffffffff);
        txtPaint.setTextSize(20);
        txtPaint.setAntiAlias(true);
    }
    public void setGridColor(int color)
    {
        gridColor = color;
    }
    public void showHorizontalGrid(boolean show)
    {
        showHorizontalGrid = show;
    }
    public void showMinAndMaxValues(boolean show)
    {
        showMinAndMax = show;
    }
    public void setTextColor(int color)
    {
        txtPaint.setColor(color);
    }
    public void setTextSize(float s)
    {
        txtPaint.setTextSize(s);
    }
    public void setMinY(float minY){
        this.minY = minY;
    }

    public void setClickRange(float clickRange) {
        this.clickRange = clickRange;
    }

    public float getClickRange() {
        return clickRange;
    }

    public void setPointSize(float pointSize) {
        this.pointSize = pointSize;
    }

    public float getPointSize() {
        return pointSize;
    }

    public void setStrokeWidth(float strokeWidth) {
        this.strokeWidth = strokeWidth;
    }

    public float getStrokeWidth() {
        return strokeWidth;
    }

    public void setPointColor(int pointColor) {
        this.pointColor = pointColor;
    }

    public int getPointColor() {
        return pointColor;
    }

    public void setBottomPadding(float bottomPadding) {
        this.bottomPadding = bottomPadding;
    }

    public float getBottomPadding() {
        return bottomPadding;
    }

    public void setSidePadding(float sidePadding) {
        this.sidePadding = sidePadding;
        update();
    }

    public float getSidePadding() {
        return sidePadding;
    }

    public void setTopPadding(float topPadding) {
        this.topPadding = topPadding;
    }

    public float getTopPadding() {
        return topPadding;
    }

    public void setBottomLineColor(int bottomLineColor) {
        this.bottomLineColor = bottomLineColor;
    }

    public int getBottomLineColor() {
        return bottomLineColor;
    }

    public void setBottomLineStrokeWidth(float bottomLineStrokeWidth) {
        this.bottomLineStrokeWidth = bottomLineStrokeWidth;
    }

    public float getBottomLineStrokeWidth() {
        return bottomLineStrokeWidth;
    }

    public void setMinYZero(boolean minYZero) {
        this.minYZero = minYZero;
    }

    public void setMarginBottom(float marginBottom) {
        this.marginBottom = marginBottom;
    }

    public float getMarginBottom() {
        return marginBottom;
    }

    public void setBottomTextBold(boolean bottomTextBold) {
        this.bottomTextBold = bottomTextBold;
    }

    public void setBottomTextColor(int bottomTextColor) {
        this.bottomTextColor = bottomTextColor;
    }

    public int getBottomTextColor() {
        return bottomTextColor;
    }

    public void setLabelSidePadding(float labelSidePadding) {
        this.labelSidePadding = labelSidePadding;
    }

    public float getLabelSidePadding() {
        return labelSidePadding;
    }

    public void setLabelBottomPadding(float labelBottomPadding) {
        this.labelBottomPadding = labelBottomPadding;
    }

    public float getLabelBottomPadding() {
        return labelBottomPadding;
    }

    public void setGuideStrokeWidth(float guideStrokeWidth) {
        this.guideStrokeWidth = guideStrokeWidth;
    }

    public float getGuideStrokeWidth() {
        return guideStrokeWidth;
    }

    public void setGuideStrokeColor(int guideStrokeColor) {
        this.guideStrokeColor = guideStrokeColor;
    }

    public int getGuideStrokeColor() {
        return guideStrokeColor;
    }

    public void setGuideMargin(float guideMargin) {
        this.guideMargin = guideMargin;
    }

    public float getGuideMargin() {
        return guideMargin;
    }

    public void setShowBottomLine(boolean showBottomLine) {
        this.showBottomLine = showBottomLine;
    }

    public void setPointTextColor(int pointTextColor) {
        this.pointTextColor = pointTextColor;
    }

    public int getPointTextColor() {
        return pointTextColor;
    }

    public void setTextBoxPadding(int textBoxPadding) {
        this.textBoxPadding = textBoxPadding;
    }

    public int getTextBoxPadding() {
        return textBoxPadding;
    }

    public void setTextBoxColor(int textBoxColor) {
        this.textBoxColor = textBoxColor;
    }

    public int getTextBoxColor() {
        return textBoxColor;
    }

    public void setTextBoxTextColor(int textBoxTextColor) {
        this.textBoxTextColor = textBoxTextColor;
    }

    public int getTextBoxTextColor() {
        return textBoxTextColor;
    }

    public void setMaxTextBoxColor(int maxTextBoxColor) {
        this.maxTextBoxColor = maxTextBoxColor;
    }

    public int getMaxTextBoxColor() {
        return maxTextBoxColor;
    }

    public void setStartPadding(int startPadding) {
        this.startPadding = startPadding;
    }

    public void setEndPadding(int endPadding) {
        this.endPadding = endPadding;
    }

    public void setBoxTextSize(float boxTextSize) {
        this.boxTextSize = boxTextSize;
    }

    public float getBoxTextSize() {
        return boxTextSize;
    }

    public void setGuideTextSize(float guideTextSize) {
        this.guideTextSize = guideTextSize;
    }

    public float getGuideTextSize() {
        return guideTextSize;
    }

    public void setGuideTextColor(int guideTextColor) {
        this.guideTextColor = guideTextColor;
    }

    public int getGuideTextColor() {
        return guideTextColor;
    }



    public void update()
    {
        shouldUpdate = true;
        postInvalidate();
    }
    public void removeAllLines(){
        while (lines.size() > 0){
            lines.remove(0);
        }
        shouldUpdate = true;
        postInvalidate();
    }

    public void addLine(Line line) {
        lines.add(line);
        shouldUpdate = true;
        postInvalidate();
    }
    public ArrayList<Line> getLines() {
        return lines;
    }
    public void setLineToFill(int indexOfLine) {
        this.lineToFill = indexOfLine;
        shouldUpdate = true;
        postInvalidate();
    }
    public int getLineToFill(){
        return lineToFill;
    }
    public void setLines(ArrayList<Line> lines) {
        this.lines = lines;
    }
    public Line getLine(int index) {
        return lines.get(index);
    }
    public int getSize(){
        return lines.size();
    }

    public void setRangeY(float min, float max) {
        minY = min;
        maxY = max;
        isMaxYUserSet = true;
    }
    public float getMaxY(){
        if (isMaxYUserSet){
            return maxY;
        } else {
            maxY = lines.get(0).getPoint(0).getY();
            for (Line line : lines){
                for (LinePoint point : line.getPoints()){
                    if (point.getY() > maxY){
                        maxY = point.getY();
                    }
                }
            }
            return maxY;
        }
    }
    public float getMinY(){
        if (isMaxYUserSet){
            float min = lines.get(0).getPoint(0).getY();
            for (Line line : lines){
                for (LinePoint point : line.getPoints()){
                    if (point.getY() < min) {
                        min = point.getY();
                        if (min == 0 && minYZero) {
                            minY = 0;
                            return minY;
                        }
                    }
                }
            }
            return minY;
        } else {
            float min = lines.get(0).getPoint(0).getY();
            for (Line line : lines){
                for (LinePoint point : line.getPoints()){
                    if (point.getY() < min) {
                        min = point.getY();
                    }
                }
            }
            minY = min;
            return minY;
        }
    }
    public float getMaxX(){
        float max = lines.get(0).getPoint(0).getX();
        for (Line line : lines){
            for (LinePoint point : line.getPoints()){
                if (point.getX() > max) max = point.getX();
            }
        }
        maxX = max;
        return maxX;

    }
    public float getMinX(){
        float max = lines.get(0).getPoint(0).getX();
        for (Line line : lines){
            for (LinePoint point : line.getPoints()){
                if (point.getX() < max) max = point.getX();
            }
        }
        maxX = max;
        return maxX;
    }

    public float getRealMaxY(){
        realMaxY = lines.get(0).getPoint(0).getY();
        for (Line line : lines){
            for (LinePoint point : line.getPoints()){
                if (point.getY() > realMaxY){
                    realMaxY = point.getY();
                }
            }
        }
        return realMaxY;
    }

    public float getRealMinY(){
        realMaxY = lines.get(0).getPoint(0).getY();
        for (Line line : lines){
            for (LinePoint point : line.getPoints()){
                if (point.getY() < realMinY){
                    realMinY = point.getY();
                }
            }
        }
        return realMinY;
    }

    public void onDraw(Canvas ca) {
        if (fullImage == null || shouldUpdate) {
            fullImage = Bitmap.createBitmap(getWidth(), getHeight(), Config.ARGB_8888);
            Canvas canvas = new Canvas(fullImage);
            String max = (int)maxY+"";// used to display max
            String min = (int)minY+"";// used to display min
            paint.reset();
            Path path = new Path();

            Rect lRect = new Rect();
            txtPaint.setTextSize(labelSize);
            txtPaint.getTextBounds("올", 0, 1, lRect);
            int labelHeight = lRect.bottom-lRect.top;

            if (this.showMinAndMax)
                sidePadding = txtPaint.measureText(max);
            if (labelHeight*1.5+marginBottom > bottomPadding) {
                bottomPadding = labelHeight*1.5f+marginBottom;
            }

            if (topPadding < boxTextSize*2.5f+textBoxPadding*2) {
                topPadding = boxTextSize*2.5f+textBoxPadding*2;
                Log.d("99_", "탑패딩:"+topPadding);
            }

            float realMaxY = getRealMaxY();
            float realMinY = getRealMinY();

            float allGap = realMaxY-realMinY;
            int gap = (int) Math.floor((realMaxY-realMinY)/4)+1;
            int midValue = (int) Math.ceil((realMaxY+realMinY)/2);

            float guideMinY = 0;

            if (midValue-2*gap <= 0) {
                guideMinY = 0;
            } else {
                guideMinY = midValue-2*gap;
            }

            Rect rcGuide = new Rect();
            txtPaint.setTextSize(guideTextSize);
            txtPaint.setTextAlign(Align.RIGHT);
            txtPaint.getTextBounds(String.valueOf((int)(guideMinY+gap*4)), 0, String.valueOf((int)(guideMinY+gap*4)).length(), rcGuide);
            int gtWidth = rcGuide.right - rcGuide.left;
            int gtHeight = rcGuide.bottom - rcGuide.top;

            if (gtWidth > startPadding) {
                startPadding = gtWidth+10;
            }

            float usableHeight = getHeight() - bottomPadding - topPadding;
            float usableWidth = getWidth() - startPadding - endPadding - sidePadding*2;
            float lineSpace = usableHeight/10;

            int guideGapPixels = Math.round(usableHeight/4);
            float yLineMin = getHeight()-bottomPadding;

            paint.setColor(getGuideStrokeColor());
            paint.setStrokeWidth(2);

            NumberFormatEditor editor = new NumberFormatEditor();

            txtPaint.setColor(guideTextColor);

            for (int i=0;i<5;i++) {
                canvas.drawText(editor.transfromNumber(guideMinY+gap*i), gtWidth, yLineMin-(guideGapPixels*i)+gtHeight/2, txtPaint);
                canvas.drawLine(startPadding, yLineMin-(guideGapPixels*i), getWidth()-endPadding, yLineMin-(guideGapPixels*i), paint);
            }
            txtPaint.setTextAlign(Align.LEFT);
            canvas.drawText(startDateStr, startPadding, topPadding/2, txtPaint);
            txtPaint.setTextAlign(Align.RIGHT);
            canvas.drawText(endDateStr, getWidth()-endPadding, topPadding/2, txtPaint);

            int lineCount = 0;
            for (Line line : lines){
                int count = 0;
                float lastXPixels = 0, newYPixels;
                float lastYPixels = 0, newXPixels;
                float maxY = getMaxY();
                float minY = getMinY();
                float maxX = getMaxX();
                float minX = getMinX();

                if (lineCount == lineToFill){
                    paint.setColor(Color.BLACK);
                    paint.setAlpha(30);
                    paint.setStrokeWidth(2);
                    for (int i = 10; i-getWidth() < getHeight(); i = i+20){
                        canvas.drawLine(i, getHeight()-bottomPadding, 0, getHeight()-(bottomPadding+getPointSize()*zeroMarginMultiflier)-i, paint);
                    }

                    paint.reset();

                    paint.setXfermode(new PorterDuffXfermode(android.graphics.PorterDuff.Mode.CLEAR));
                    for (LinePoint p : line.getPoints()){
                        float yPercent = (p.getY()-minY)/(maxY - minY);
                        float xPercent = (p.getX()-minX)/(maxX - minX);
                        if (count == 0){
                            lastXPixels = sidePadding + (xPercent*usableWidth);
                            lastYPixels = getHeight() - (bottomPadding+getPointSize()*zeroMarginMultiflier) - (usableHeight*yPercent);
                            path.moveTo(lastXPixels, lastYPixels);
                        } else {
                            newXPixels = sidePadding + (xPercent*usableWidth);
                            newYPixels = getHeight() - (bottomPadding+getPointSize()*zeroMarginMultiflier) - (usableHeight*yPercent);
                            path.lineTo(newXPixels, newYPixels);
                            Path pa = new Path();
                            pa.moveTo(lastXPixels, lastYPixels);
                            pa.lineTo(newXPixels, newYPixels);
                            pa.lineTo(newXPixels, 0);
                            pa.lineTo(lastXPixels, 0);
                            pa.close();
                            canvas.drawPath(pa, paint);
                            lastXPixels = newXPixels;
                            lastYPixels = newYPixels;
                        }
                        count++;
                    }

                    path.reset();

                    path.moveTo(0, getHeight()-bottomPadding);
                    path.lineTo(sidePadding, getHeight()-bottomPadding);
                    path.lineTo(sidePadding, 0);
                    path.lineTo(0, 0);
                    path.close();
                    canvas.drawPath(path, paint);

                    path.reset();

                    path.moveTo(getWidth(), getHeight()-bottomPadding);
                    path.lineTo(getWidth()-sidePadding, getHeight()-bottomPadding);
                    path.lineTo(getWidth()-sidePadding, 0);
                    path.lineTo(getWidth(), 0);
                    path.close();

                    canvas.drawPath(path, paint);

                }

                lineCount++;
            }

            paint.reset();

            if (showBottomLine) {
                paint.setColor(getBottomLineColor());
                paint.setStrokeWidth(getBottomLineStrokeWidth());
                paint.setStrokeCap(Paint.Cap.ROUND);
                paint.setAntiAlias(true);
                float drawPadding = sidePadding / 1.5f;
                canvas.drawLine(drawPadding, getHeight() - bottomPadding, (getWidth() - drawPadding), getHeight() - bottomPadding, paint);
            }
            paint.reset();
            paint.setColor(this.gridColor);
            paint.setAlpha(50);
            paint.setAntiAlias(true);
            if(this.showHorizontalGrid) {
                for (int i = 1; i <= 10; i++) {
                    canvas.drawLine(sidePadding, getHeight() - bottomPadding - (i * lineSpace), getWidth(), getHeight() - bottomPadding - (i * lineSpace), paint);
                }
            }

            paint.setAlpha(255);
            txtPaint.setTextAlign(Align.CENTER);
            txtPaint.setTextSize(labelSize);

            if (bottomTextBold) {
                paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
            }

            for (Line line : lines){
                int count = 0;
                float lastXPixels = 0, newYPixels;
                float lastYPixels = 0, newXPixels;
                float maxY = getMaxY();
                float minY = getMinY();
                float maxX = getMaxX();
                float minX = getMinX();

                paint.setStrokeWidth(getStrokeWidth());

                for (LinePoint p : line.getPoints()){
                    paint.setColor(line.getColor());
                    paint.setStrokeWidth(getStrokeWidth());
                    //float yPercent = (p.getY()-minY)/(maxY - minY);
                    float yFactor = (p.getY()-guideMinY)/gap;
                    float oneSpace = usableHeight/4;
                    float xPercent = (p.getX()-minX)/(maxX - minX);
                    //float yPixels = getHeight() - (bottomPadding+getPointSize()*zeroMarginMultiflier) - (usableHeight*yPercent);
                    if (count == 0){
                        lastXPixels = startPadding + sidePadding + (xPercent*usableWidth);
                        lastYPixels = getHeight() - bottomPadding - (oneSpace*yFactor);
                        if (line.getPoints().size() == 1) {
                            lastXPixels = startPadding+sidePadding+usableWidth/2;
                        }
                    } else {
                        newXPixels = startPadding + sidePadding + (xPercent*usableWidth);
                        newYPixels = getHeight() - bottomPadding - (oneSpace*yFactor);
                        canvas.drawLine(lastXPixels, lastYPixels, newXPixels, newYPixels, paint);
                        lastXPixels = newXPixels;
                        lastYPixels = newYPixels;
                    }
                    if (p.getLabel_string()!=null && !hasShownMax) {
                        /*Path labelPath = new Path();
                        Rect rc = new Rect();
                        paint.getTextBounds(p.getLabel_string(), 0, p.getLabel_string().length(), rc);
                        paint.setColor(bottomLineColor);
                        float txtWidth = rc.right-rc.left;
                        float txtHeight = rc.bottom-rc.top;
                        float x, y;
                        x = lastXPixels-(txtWidth/2)-labelSidePadding; y = usableHeight+bottomPadding;
                        labelPath.moveTo(x, y);
                        x += txtWidth+getLabelSidePadding()*2;
                        labelPath.lineTo(x, y);
                        y -= txtHeight;
                        labelPath.lineTo(x, y);
                        x = lastXPixels;
                        y = usableHeight;
                        labelPath.lineTo(x, y);
                        x -= (txtWidth/2)+labelSidePadding;
                        y = +bottomPadding;
                        labelPath.lineTo(x, y);
                        y = usableHeight+bottomPadding;
                        labelPath.lineTo(x, y);
                        labelPath.close();
                        canvas.drawPath(labelPath, paint);*/
                        if (indexSelected2 == count || (indexSelected2 == -1 && p.getY() == getRealMaxY())) {
                            paint.setColor(getGuideStrokeColor());
                            paint.setStrokeWidth(getGuideStrokeWidth());
                            paint.setStrokeCap(Paint.Cap.ROUND);
                            canvas.drawLine(lastXPixels, lastYPixels + getPointSize() * 1.5f, lastXPixels, usableHeight + topPadding + 10, paint);
                            if (bottomTextColor != 0) {
                                txtPaint.setColor(bottomTextColor);
                            }
                            Rect rc = new Rect();
                            paint.getTextBounds(p.getLabel_string(), 0, p.getLabel_string().length(), rc);
                            float txtWidth = rc.right - rc.left;
                            float txtHeight = rc.bottom - rc.top;
                            if (txtWidth / 2 > lastXPixels) {
                                txtPaint.setTextAlign(Align.LEFT);
                            } else if (txtWidth / 2 > (usableWidth - lastXPixels)) {
                                txtPaint.setTextAlign(Align.RIGHT);
                            } else {
                                txtPaint.setTextAlign(Align.CENTER);
                            }
                            canvas.drawText(p.getLabel_string(), lastXPixels, getHeight()-(txtHeight/2), txtPaint);
                            if (p.getY() >= getRealMaxY()) {
                                hasShownMax = true;
                            }
                        }
                    }
                    count++;
                }
            }

            hasShownMax = false;

            int pointCount = 0;
            int pointCount2 = 0;

            for (Line line : lines){
                float maxY = getMaxY();
                float minY = getMinY();
                float maxX = getMaxX();
                float minX = getMinX();

                float betweenX = (usableWidth)/(line.getPoints().size()-1);

                paint.setColor(line.getColor());
                paint.setStrokeWidth(getStrokeWidth());
                paint.setStrokeCap(Paint.Cap.ROUND);

                if (line.isShowingPoints()){
                    for (LinePoint p : line.getPoints()){
                        //float yPercent = (p.getY()-minY)/(maxY - minY);
                        float yFactor = (p.getY()-guideMinY)/gap;
                        float oneSpace = usableHeight/4;
                        float xPercent = (p.getX()-minX)/(maxX - minX);
                        float xPixels = startPadding + sidePadding + (xPercent*usableWidth);
                        float yPixels = getHeight() - bottomPadding - (oneSpace*yFactor);
                        if (line.getPoints().size() == 1) {
                            xPixels = startPadding+sidePadding+usableWidth/2;
                        }

                        paint.setColor(getPointColor());
                        canvas.drawCircle(xPixels, yPixels, getPointSize(), paint);
                        paint.setColor(Color.WHITE);
                        canvas.drawCircle(xPixels, yPixels, getPointSize()/2, paint);

                        Path path2 = new Path();
                        path2.addCircle(xPixels, yPixels, clickRange, Direction.CW);
                        p.setPath(path2);
                        p.setRegion(new Region((int)(xPixels-clickRange), (int)(yPixels-clickRange), (int)(xPixels+clickRange), (int)(yPixels+clickRange)));

                        /*if (indexSelected2 != pointCount) {
                            txtPaint.setTextAlign(Align.CENTER);
                            txtPaint.setColor(getPointTextColor());
                            canvas.drawText(String.valueOf(Math.round(p.getY())), xPixels, yPixels-(getPointSize()*2f), txtPaint);
                        }*/

                        if (indexSelected == pointCount && listener != null){
                            paint.setColor(Color.parseColor("#33B5E5"));
                            paint.setAlpha(100);
                            Path pathRipple = new Path();
                            pathRipple.addCircle(xPixels, yPixels, getPointSize()*2, Direction.CW);
                            canvas.drawPath(pathRipple, paint);
                            paint.setAlpha(255);
                        }

                        pointCount++;
                    }
                    int lastEndX = 0;
                    int newStartX = 0;
                    int newEndX = 0;
                    for (LinePoint p : line.getPoints()) {
                        //float yPercent = (p.getY()-minY)/(maxY - minY);
                        float yFactor = (p.getY()-guideMinY)/gap;
                        float oneSpace = usableHeight/4;
                        float xPercent = (p.getX()-minX)/(maxX - minX);
                        float xPixels = startPadding + sidePadding + (xPercent*usableWidth);
                        float yPixels = getHeight() - bottomPadding - (oneSpace*yFactor);
                        if (line.getPoints().size() == 1) {
                            xPixels = startPadding+sidePadding+usableWidth/2;
                        }
                        //float yPixels = getHeight() - (bottomPadding+getPointSize()*zeroMarginMultiflier) - (usableHeight*yPercent);
                        p.setRealX((int)xPixels);
                        p.setRealY((int)yPixels);
                        Path path2 = new Path();
                        path2.addCircle(xPixels, yPixels, clickRange, Direction.CW);
                        p.setPath(path2);
                        if (pointCount2 == 0) {
                            newStartX = (int)(p.getRealX()-betweenX/2);
                            newEndX = (int)(p.getRealX()+betweenX/2);
                        } else {
                            newStartX = lastEndX;
                            newEndX = (int)(p.getRealX()+betweenX/2);
                        }
                        p.setRegion(new Region(newStartX, 0, newEndX, getHeight()));
                        lastEndX = newEndX;
                        if (indexSelected2 == pointCount2 && listener != null) {
                            if (p.getY() == getRealMaxY()) {
                                String[] text = {String.valueOf(Math.round(p.getY()))+"표", "최다 득표"};
                                drawVoteBox(canvas, p, text);
                            } else {
                                String[] text = {String.valueOf(Math.round(p.getY()))+"표"};
                                drawVoteBox(canvas, p, text);
                            }
                        }
                        if (indexSelected2 == -1 && p.getY()==getRealMaxY() && notClicked && !hasShownMax) {
                            String[] text = {String.valueOf(Math.round(p.getY()))+"표", "최다 득표"};
                            drawVoteBox(canvas, p, text);
                            hasShownMax = true;
                        }
                        pointCount2++;
                    }
                }
            }

            hasShownMax = false;
            shouldUpdate = false;
            if (this.showMinAndMax) {
                ca.drawText(max, 0, txtPaint.getTextSize(), txtPaint);
                ca.drawText(min,0,this.getHeight(),txtPaint);
            }
        }
        ca.drawBitmap(fullImage, 0, 0, null);


    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        Point point = new Point();
        point.x = (int) event.getX();
        point.y = (int) event.getY();

        int count = 0;
        int lineCount = 0;
        int pointCount;

        Region r = new Region();
        for (Line line : lines){
            pointCount = 0;
            for (LinePoint p : line.getPoints()){

                if (p.getPath() != null && p.getRegion() != null){
                    r = p.getRegion();
                    if (r.contains(point.x, point.y) && event.getAction() == MotionEvent.ACTION_DOWN) {
                        notClicked = false;
                        indexSelected = count;
                    } else if (event.getAction() == MotionEvent.ACTION_UP){
                        if (r.contains(point.x, point.y) && listener != null) {
                            listener.onClick(lineCount, pointCount);
                        }
                        indexSelected = -1;
                    } else if (r.contains(point.x, point.y) && event.getAction() == MotionEvent.ACTION_MOVE) {
                        indexSelected2 = count;
                        indexSelected = -1;
                    }
                }

                pointCount++;
                count++;
            }
            lineCount++;

        }

        if (event.getAction() == MotionEvent.ACTION_MOVE || event.getAction() == MotionEvent.ACTION_DOWN || event.getAction() == MotionEvent.ACTION_UP){
            shouldUpdate = true;
            postInvalidate();
        }

        return true;
    }

    private void drawVoteBox(Canvas ca, LinePoint p, String[] text) {
        boolean isMax = false;
        if (p.getY() >= getRealMaxY()) {
            paint.setColor(getMaxTextBoxColor());
            isMax = true;
        } else {
            paint.setColor(getTextBoxColor());
        }
        paint.setTextAlign(Align.CENTER);
        paint.setStrokeWidth(2);
        paint.setAntiAlias(true);
        txtPaint.setTextSize(boxTextSize);
        Rect rc = new Rect();
        String maxT = "";
        int tCount = 0;
        for (String t : text) {
            if (t.length() > maxT.length()) {
                maxT = t;
            }
            tCount++;
        }
        txtPaint.getTextBounds(maxT, 0, maxT.length(), rc);

        int singleLineHeight = rc.bottom-rc.top;
        float lineSpace = singleLineHeight*0.5f;

        int textWidth = rc.right-rc.left;
        int textHeight = 0;
        textHeight = singleLineHeight * text.length + Math.round(lineSpace * (text.length - 1));
        int boxWidth = Math.round(textWidth+textBoxPadding*2);
        int boxHeight = Math.round(textHeight+textBoxPadding*2);

        int tailHeight = 20;
        int tailWidth = 20;
        int startX = p.getRealX();
        int startY = p.getRealY();
        int radius = (int) Math.floor((double)textBoxPadding/7.5d);

        if (startX < Math.ceil(boxWidth/2)) {
            Path path = new Path();
            path.moveTo(startX, startY);
            path.rLineTo(0, -(textHeight+tailHeight+textBoxPadding));
            path.rQuadTo(radius, -(textBoxPadding-radius), textBoxPadding, -textBoxPadding);
            path.rLineTo(textWidth, 0);
            path.rQuadTo(textBoxPadding-radius, radius, textBoxPadding, textBoxPadding);
            path.rLineTo(0, textHeight);
            path.rQuadTo(-radius, textBoxPadding-radius, -textBoxPadding, textBoxPadding);
            path.rLineTo(-((textWidth+textBoxPadding)-tailWidth), 0);
            path.lineTo(startX, startY);
            path.close();
            ca.drawPath(path, paint);
            txtPaint.setColor(getTextBoxTextColor());
            txtPaint.setTextAlign(Align.CENTER);
            int cnt = 0;
            for (String t: text) {
                if (cnt == 0) {
                    ca.drawText(t, (startX+Math.round(boxWidth/2)), (startY-tailHeight-textBoxPadding), txtPaint);
                } else {
                    ca.drawText(t, (startX+Math.round(boxWidth/2)), (startY-tailHeight-textBoxPadding)-(singleLineHeight+lineSpace)*cnt, txtPaint);
                }
                cnt++;
            }
        } else if ((getWidth()-startX) < Math.ceil(boxWidth/2)) {
            Path path = new Path();
            path.moveTo(startX, startY);
            path.rLineTo(0, -(tailHeight+getTextBoxPadding()+textHeight));
            path.rQuadTo(-radius, -(textBoxPadding-radius), -textBoxPadding, -textBoxPadding);
            path.rLineTo(-textWidth, 0);
            path.rQuadTo(-(textBoxPadding-radius), radius, -textBoxPadding, textBoxPadding);
            path.rLineTo(0, textHeight);
            path.rQuadTo(radius, textBoxPadding-radius, textBoxPadding, textBoxPadding);
            path.rLineTo(textWidth+textBoxPadding-tailWidth, 0);
            path.lineTo(startX, startY);
            path.close();
            ca.drawPath(path, paint);
            txtPaint.setColor(getTextBoxTextColor());
            txtPaint.setTextAlign(Align.CENTER);
            int cnt = 0;
            for (String t: text) {
                if (cnt == 0) {
                    ca.drawText(t, (startX-Math.round(boxWidth/2)), (startY-tailHeight-textBoxPadding), txtPaint);
                } else {
                    ca.drawText(t, (startX-Math.round(boxWidth/2)), (startY-tailHeight-textBoxPadding)-(singleLineHeight+lineSpace)*cnt, txtPaint);
                }
                cnt++;
            }
        } else {
            Path path = new Path();
            path.moveTo(startX, startY);
            path.rLineTo(-tailWidth, -tailWidth);
            path.rLineTo(-textWidth/2+tailWidth, 0);
            path.rQuadTo(-(textBoxPadding+radius), -radius, -textBoxPadding, -textBoxPadding);
            path.rLineTo(0, -textHeight);
            path.rQuadTo(radius, -(textBoxPadding-radius), textBoxPadding, -textBoxPadding);
            path.rLineTo(textWidth, 0);
            path.rQuadTo(textBoxPadding-radius, radius, textBoxPadding, textBoxPadding);
            path.rLineTo(0, textHeight);
            path.rQuadTo(-radius, textBoxPadding-radius, -textBoxPadding, textBoxPadding);
            path.rLineTo(-textWidth/2+tailWidth, 0);
            path.lineTo(startX, startY);
            path.close();
            ca.drawPath(path, paint);
            txtPaint.setColor(getTextBoxTextColor());
            txtPaint.setTextAlign(Align.CENTER);
            int cnt = 0;
            for (String t: text) {
                if (cnt == 0) {
                    ca.drawText(t, startX, (startY-tailHeight-textBoxPadding), txtPaint);
                } else {
                    ca.drawText(t, startX, (startY-tailHeight-textBoxPadding)-(singleLineHeight+lineSpace)*cnt, txtPaint);
                }
                cnt++;
            }
        }
    }

    public void setOnPointClickedListener(OnPointClickedListener listener) {
        this.listener = listener;
    }

    public int getLabelSize() {
        return labelSize;
    }
    public void setLabelSize(int labelSize) {
        this.labelSize = labelSize;
    }

    @Override
    public void update(Observable o, Object arg) {

        VoteHistoryAdapter adapter = (VoteHistoryAdapter) o;
        this.startDateStr = adapter.startDateStr;
        this.endDateStr = adapter.endDateStr;
        lines.clear();

        reset();

        Line l = new Line();
        l.setColor(adapter.getLineColor());
        int count = 0;

        for (GraphDTO item : adapter.getGeneratedData()) {
            LinePoint p = new LinePoint(count, item.getVoteCount());
            p.setLabel_string(item.getLabel());
            l.addPoint(p);
            count++;
        }

        lines.add(l);
        update();

    }
    
    private void reset() {
        indexSelected = -1;
        indexSelected2 = -1;
        notClicked = true;
        hasShownMax = false;
    }

    public interface OnPointClickedListener {
        abstract void onClick(int lineIndex, int pointIndex);
    }

    public void setAdapter(VoteHistoryAdapter adapter) {
        adapter.addObserver(this);
    }
}
