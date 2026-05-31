# Deliveries

Deliveries is an app that lets you track your parcels from various providers with ease.

<!--
<p align="center">
<a href="https://play.google.com/store/apps/details?id=dev.itsvic.parceltracker">
<img src="./.github/play-badge.png" alt="Get it on Google Play" height="48dp">
</a>
<a href="https://f-droid.org/packages/dev.itsvic.parceltracker">
<img src="https://f-droid.org/badge/get-it-on.png" alt="Get it on F-Droid" height="48dp">
</a>
<a href="https://apt.izzysoft.de/fdroid/index/apk/dev.itsvic.parceltracker">
<img src="https://gitlab.com/IzzyOnDroid/repo/-/raw/master/assets/IzzyOnDroidButtonGreyBorder_nofont.png" alt="Get it on IzzyOnDroid" height="48dp">
</a>
</p>

<p align="center">
<a href="https://discord.gg/QdvpveRTsT">
<img src="https://img.shields.io/discord/1349842428366159973?style=for-the-badge&logo=discord&logoColor=white&color=%235865F2" alt="Join our Discord">
</a>
<a href="https://matrix.to/#/#parcel-community:matrix.org">
<img src="https://img.shields.io/matrix/parcel-community%3Amatrix.org?style=for-the-badge&logo=matrix&color=white" alt="Join the Matrix room">
</a>
</p> -->

## Contributing

We use `ktfmt` for formatting files. For ease of use, we included the sample editorconfig that comes with `ktfmt`, as well as a helper script to invoke it.
To format all the code, simply run `./scripts/ktfmt.sh .`. It will download `ktfmt` if necessary.

Similarly, we have `./scripts/sort-strings.sh` to sort translation files by key. This script uses Nix to pull in `xsltproc` from `libxslt`.

## Supported services

This app exclusively uses the [Binderbyte API](https://binderbyte.com/) for tracking and requires users to manually input their own API Key in the Settings menu.

Indonesia (Binderbyte API):

- JNE
- POS Indonesia
- J&T Express
- J&T Cargo
- SiCepat
- TIKI
- AnterAja
- Wahana
- Ninja Xpress
- Lion Parcel
- PCP Express
- JET Express
- REX Express
- First Logistics
- ID Express
- Shopee Express
- KGXpress
- SAP Express
- JX Express
- RPX
- Lazada eLogistics
- Indah Cargo
- Dakota Cargo
- Kurir Rekomendasi Tokopedia
