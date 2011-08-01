package com.google.forplay.sample.pathdrawing.html;

import com.google.forplay.sample.pathdrawing.core.PathDrawingGame;

import forplay.core.ForPlay;
import forplay.html.HtmlAssetManager;
import forplay.html.HtmlGame;
import forplay.html.HtmlPlatform;

public class PathDrawingEntryHtml extends HtmlGame {
	@Override
	public void start() {
		HtmlAssetManager assets = HtmlPlatform.register().assetManager();
		assets.setPathPrefix("pathdrawing/");
		ForPlay.run(new PathDrawingGame());
	}
}

