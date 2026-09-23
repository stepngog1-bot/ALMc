# سامانه فروش شرکت شیشه ایمنی الماس نگین بینالود — v1.16

Native Android app (Kotlin + Jetpack Compose, Material 3 Expressive), Persian RTL, light mode only,
target 412×892dp portrait phones.

## What v1.16 adds (over v1.15's kept catalog/cart/checkout logic)
- Fixed customer number assigned at registration; order codes are `ALM-{شماره مشتری}-{ردیف}`.
- Registration/order times are stored as UTC millis and displayed converted to Jalali date + Iran
  clock time (`util/JalaliDate.kt`, Asia/Tehran).
- Professional account screen: پشتیبانی (dialer), درخواست تغییر شماره همراه, تغییر رمز عبور,
  ارتباط با واحد فروش (dialer), خروج.

## Build
1. Open this folder in Android Studio (Koala/Ladybug or newer).
2. Let Gradle sync — it needs the standard Google/Maven Central repos (this was written and
   reviewed outside an environment with SDK network access, so it has **not** been compiled here;
   double-check the sync and a debug build locally first).
3. `Build > Generate Signed Bundle / APK…` → APK → point it at your keystore to produce the
   signed release APK.

## Notes / things to double check on your machine
- No logo image was supplied for the splash/registration-success screens; a placeholder teal
  circle + icon stands in for the provided 136×136dp image. Drop your real asset into
  `res/drawable` and swap it into `screens/AuthScreens.kt`.
- The product catalog (8 glass parts × 4 car models) is seeded once into Room on first launch as
  real catalog data — adjust names/prices in `data/AppDatabase.kt` (`CatalogSeed`) to match your
  actual price list.
- Cash payment gateway is intentionally inert (per spec) — the "رفتن به درگاه پرداخت" button on
  the demo screen has no handler yet.
- Passwords are hashed with SHA-256 locally; there's no backend/sync in this build — all data
  (customers, catalog, cart, orders, phone-change requests) lives in a local Room database on the
  device.
