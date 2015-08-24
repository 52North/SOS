def arr = new ArrayList<String>()
arr += doc["getcapabilities-sections"].values
return arr.join("_")