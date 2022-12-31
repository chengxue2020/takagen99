# Box (supports only Android 5 and above)

=== Source Code - Editing the app default settings ===
/src/main/java/com/github/tvbox/osc/base/App.java

    private void initParams() {

        putDefault(HawkConfig.HOME_REC, 2);       // Home Rec 0=豆瓣, 1=推荐, 2=历史
        putDefault(HawkConfig.PLAY_TYPE, 1);      // Player   0=系统, 1=IJK, 2=Exo
        putDefault(HawkConfig.IJK_CODEC, "硬解码");// IJK Render 软解码, 硬解码
        putDefault(HawkConfig.HOME_SHOW_SOURCE, true);  // true=Show, false=Not show
        putDefault(HawkConfig.HOME_NUM, 2);       // History Number
        putDefault(HawkConfig.DOH_URL, 2);        // DNS
        putDefault(HawkConfig.SEARCH_VIEW, 2);    // Text or Picture

    }

=== Setting Up the Configuration Address ===
- 数据源 > Input Source URL address
- 直播 (Optional) > Input Live URL (http) address. If empty, will take Live URL from Source file
- EPG (Optional) > Input EPG URL (http) address. If empty, will take EPG URL from Source file. If not found in Source file, default from http://epg.51zmt.top:8000/api/diyp/
