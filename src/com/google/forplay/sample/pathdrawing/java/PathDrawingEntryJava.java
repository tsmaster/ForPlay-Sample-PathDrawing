package com.google.forplay.sample.pathdrawing.java;

import com.google.forplay.sample.pathdrawing.core.PathDrawingGame;

import forplay.core.ForPlay;
import forplay.java.JavaAssetManager;
import forplay.java.JavaPlatform;

public class PathDrawingEntryJava {
	public static void main(String[] args) {
		JavaAssetManager assets = JavaPlatform.register().assetManager();
		assets.setPathPrefix("src/com/google/forplay/sample/pathdrawing/resources");
		ForPlay.run(new PathDrawingGame());
	}
}
