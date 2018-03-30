package com.footprint.utils;

import com.google.gson.*;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public final class GsonUtils {
	private static Gson gson;

	public static Gson buildGson() {
		if(gson == null){
			GsonBuilder builder = new GsonBuilder();
			builder.registerTypeAdapter(Date.class, new DateTypeAdapter());
			builder.disableHtmlEscaping();
			gson = builder.create();
		}
		return gson;
	}

	private static class DateTypeAdapter implements JsonSerializer<Date>, JsonDeserializer<Date> {

		@Override
		public JsonElement serialize(Date src, Type typeOfSrc, JsonSerializationContext context) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");  
			return new JsonPrimitive(sdf.format(src));
		}

		@Override
		public Date deserialize(JsonElement json, Type type, JsonDeserializationContext cxt) {
			try {
				return new SimpleDateFormat("yyyyMMddHHmmss").parse(json.getAsString());
			} catch (ParseException e) {
				try {
					return new SimpleDateFormat("yyyyMMdd").parse(json.getAsString());
				} catch (ParseException e2) {
					throw new IllegalArgumentException("无法转换为Date类型");
				}
			}
		}
	}

}
