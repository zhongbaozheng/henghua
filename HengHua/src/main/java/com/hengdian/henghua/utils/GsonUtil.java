package com.hengdian.henghua.utils;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * Gson json方法封装
 *
 * @author Anderok
 */
public class GsonUtil {
	public static void main(String[] args) {
		String[] arr = new String[10];
		for (int i = 0; i < arr.length; i++) {
			arr[i] = "字符" + i;
		}
		String json = toJson(arr);
		System.out.println(json);
		System.out
				.println(getInt(jsonParser.parse(json).getAsJsonArray(), 1, 0));

	}

	static class A {
		String name;

		public A(String name) {
			this.name = name;
		}
	}

	/**
	 * 根据对象类型，将json字符串转化成对象
	 * 
	 * @param classOfT
	 * @param json
	 * @return
	 */
	public static <T> T toInstance(Class<T> classOfT, String json) {
		if (json == null)
			return null;

		return new Gson().fromJson(json, classOfT);
	}

	/**
	 * 将基本数据类型和对象等内容转化成json字符串
	 * 
	 * @param obj
	 * @return
	 */
	public static String toJson(Object obj) {
		if (obj == null)
			return null;

		return new Gson().toJson(obj);
	}

	private static JsonParser jsonParser = new JsonParser();

	/**
	 * 将字符串转化成Json对象
	 * 
	 * @param json
	 * @return
	 */
	public static JsonObject toJsonObject(String json) {
		if (json == null)
			return null;

		JsonElement ele = jsonParser.parse(json);
		if (ele == null || ele.isJsonNull() || !ele.isJsonObject())
			return null;

		return ele.getAsJsonObject();
	}

	/**
	 * 将字符串转化成Json数组
	 * 
	 * @param json
	 * @return
	 */
	public static JsonArray toJsonArray(String json) {
		if (json == null)
			return null;

		JsonElement ele = jsonParser.parse(json);
		if (ele == null || ele.isJsonNull() || !ele.isJsonArray())
			return null;

		return ele.getAsJsonArray();
	}

	public static int getInt(JsonObject jsonObject, String key, int defVal) {
		if (jsonObject == null)
			return defVal;

		JsonElement ele = jsonObject.get(key);
		if (ele == null || ele.isJsonNull() || !ele.isJsonPrimitive())
			return defVal;

		return ele.getAsInt();

	}

	public static long getLong(JsonObject jsonObject, String key, long defVal) {
		if (jsonObject == null)
			return defVal;

		JsonElement ele = jsonObject.get(key);
		if (ele == null || ele.isJsonNull() || !ele.isJsonPrimitive())
			return defVal;

		return ele.getAsLong();
	}

	public static double getDouble(JsonObject jsonObject, String key,
			double defVal) {
		if (jsonObject == null)
			return defVal;

		JsonElement ele = jsonObject.get(key);
		if (ele == null || ele.isJsonNull() || !ele.isJsonPrimitive())
			return defVal;

		return ele.getAsDouble();
	}

	public static String getString(JsonObject jsonObject, String key,
			String defVal) {
		if (jsonObject == null)
			return defVal;

		JsonElement ele = jsonObject.get(key);
		if (ele == null || ele.isJsonNull() || !ele.isJsonPrimitive())
			return defVal;

		return ele.getAsString();
	}

	public static boolean getBoolean(JsonObject jsonObject, String key,
			boolean defVal) {
		if (jsonObject == null)
			return defVal;

		JsonElement ele = jsonObject.get(key);
		if (ele == null || ele.isJsonNull() || !ele.isJsonPrimitive())
			return defVal;

		return ele.getAsBoolean();
	}

	public static JsonObject getJsonObject(JsonObject jsonObject, String key) {
		if (jsonObject == null)
			return null;

		JsonElement ele = jsonObject.get(key);
		if (ele == null || ele.isJsonNull() || !ele.isJsonObject())
			return null;

		return ele.getAsJsonObject();

	}

	public static JsonArray getJsonArray(JsonObject jsonObject, String key) {
		if (jsonObject == null)
			return null;

		JsonElement ele = jsonObject.get(key);
		if (ele == null || ele.isJsonNull() || !ele.isJsonArray())
			return null;

		return ele.getAsJsonArray();

	}

	public static int getInt(JsonArray jsonArray, int index, int defVal) {
		if (jsonArray == null)
			return defVal;

		JsonElement ele = jsonArray.get(index);
		if (ele == null || ele.isJsonNull() || !ele.isJsonPrimitive())
			return defVal;

		return ele.getAsInt();
	}

	public static long getLong(JsonArray jsonArray, int index, long defVal) {
		if (jsonArray == null)
			return defVal;

		JsonElement ele = jsonArray.get(index);
		if (ele == null || ele.isJsonNull() || !ele.isJsonPrimitive())
			return defVal;

		return ele.getAsLong();
	}

	public static double getDouble(JsonArray jsonArray, int index, double defVal) {
		if (jsonArray == null)
			return defVal;

		JsonElement ele = jsonArray.get(index);
		if (ele == null || ele.isJsonNull() || !ele.isJsonPrimitive())
			return defVal;

		return ele.getAsDouble();
	}

	public static String getString(JsonArray jsonArray, int index, String defVal) {
		if (jsonArray == null)
			return defVal;

		JsonElement ele = jsonArray.get(index);
		if (ele == null || ele.isJsonNull() || !ele.isJsonPrimitive())
			return defVal;

		return ele.getAsString();
	}

	public static boolean getBoolean(JsonArray jsonArray, int index,
			boolean defVal) {
		if (jsonArray == null)
			return defVal;

		JsonElement ele = jsonArray.get(index);
		if (ele == null || ele.isJsonNull() || !ele.isJsonPrimitive())
			return defVal;

		return ele.getAsBoolean();
	}

	public static JsonObject getJsonObject(JsonArray jsonArray, int index) {
		if (jsonArray == null)
			return null;

		JsonElement ele = jsonArray.get(index);
		if (ele == null || ele.isJsonNull() || !ele.isJsonObject())
			return null;

		return ele.getAsJsonObject();
	}

	public static JsonArray getJsonArray(JsonArray jsonArray, int index) {
		if (jsonArray == null)
			return null;

		JsonElement ele = jsonArray.get(index);
		if (ele == null || ele.isJsonNull() || !ele.isJsonArray())
			return null;

		return ele.getAsJsonArray();
	}
}
