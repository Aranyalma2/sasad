## 🛰️ Hálózati réteg

### `data.remote` 📁

- **WeatherApiService.kt**  
  Retrofit interfész az `https://api.open-meteo.com/v1/forecast` híváshoz.

- **LocationApiService.kt**  
  Retrofit interfész a `https://geocoding-api.open-meteo.com/v1/search` híváshoz.

---

### `data.repository` 📁

- **WeatherRepository.kt**  
  Időjárás előrejelzés lekérése, domain modellé konvertálás.

- **LocationRepository.kt**  
  Városok lekérése, `LocationEntity`-re konvertálás.

---

### `di.NetworkModule.kt` 📁

- **NetworkModule**  
  Retrofit példányok biztosítása Hilt segítségével (`@Provides`, `@Singleton`).

---

## 🗂️ ORM - Lokális adatbázis réteg (Room)

### `data.local` 📁

- **LocationEntity.kt**
  - `@Entity`: Mentett városok tárolása, név, ország, földrajzi koordináták.

- **DailyWeatherEntity.kt**
  - `@Entity`: Egy városhoz tartozó napi időjárási adatok tárolása (max/min hőmérséklet, csapadék, szél stb.).

- **HourlyWeatherEntity.kt**
  - `@Entity`: Egy városhoz tartozó óránkénti időjárási adatok (hőmérséklet, csapadék, szél, páratartalom stb.).

- **WeatherDao.kt**
  - DAO interfész, amely lehetővé teszi a napi és óránkénti időjárási adatok beszúrását, lekérdezését és törlését.
  - Műveletek: beszúrás `DailyWeatherEntity`, `HourlyWeatherEntity`, lekérés város szerint, teljes törlés stb.

- **LocationDao.kt**
  - DAO interfész, amely lehetővé teszi a `LocationEntity` elemek CRUD műveleteit.
  - Műveletek: összes város lekérése, egy város beszúrása vagy törlése.

- **LocationWithWeather.kt**
  - `@Relation`-t tartalmazó adatosztály, amely egy városhoz kapcsolt napi és órás előrejelzést ad vissza.
  - Aggregált nézet a városról és időjárásáról egy adatmodellben.

- **WeatherDatabase.kt**
  - `@Database`: A Room adatbázis inicializálása, amely regisztrálja az entitásokat (`LocationEntity`, `DailyWeatherEntity`, `HourlyWeatherEntity`) és DAO-kat (`LocationDao`, `WeatherDao`).


## 🔁 Mapperek

### `data.mapper` 📁

- **LocationResponse.toLocationEntities()**  
  Egy `LocationResponse` objektumot konvertál `List<LocationEntity>` típusra Room mentéshez.

- **CombinedWeather.toHourlyEntities(locationId: Int)**  
  `CombinedWeather` órás adatait alakítja át `HourlyWeatherEntity` listává az adott város ID-hoz.

- **CombinedWeather.toDailyEntities(locationId: Int)**  
  `CombinedWeather` napi adatait alakítja át `DailyWeatherEntity` listává az adott város ID-hoz.

- **WeatherResponse.toHourly()**  
  `WeatherResponse` → `List<HourlyWeather>`: Az API válaszból köztes domain-modell.

- **WeatherResponse.toDaily()**  
  `WeatherResponse` → `List<DailyWeather>`: Az API válaszból köztes domain-modell.

