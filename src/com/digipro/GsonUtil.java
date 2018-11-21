package com.digipro;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Created by James on 10/02/2015.
 */
public class GsonUtil {
	public static final GsonBuilder builder = new GsonBuilder();
	public static final Gson gson = builder.create();
}
