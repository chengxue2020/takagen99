# TVBoxOSC

![Build](https://shields.io/github/actions/workflow/status/chengxue2020-org/Box/Action.yml?branch=master&logo=github&label=Build)
[![Channel](https://img.shields.io/badge/Follow-Telegram-blue.svg?logo=telegram)](https://t.me/TVBoxOSC)
[![Download](https://img.shields.io/github/v/release/chengxue2020-org/Box?color=orange&logoColor=orange&label=Download&logo=DocuSign)](https://github.com/chengxue2020-org/Box/releases/latest) 
[![Total](https://shields.io/github/downloads/chengxue2020-org/Box/total?logo=Bookmeter&label=Counts&logoColor=yellow&color=yellow)](https://github.com/chengxue2020-org/Box/releases)

## Credits
This repo relies on the following third-party projects:
- [CatVodTVOfficial/TVBoxOSC](https://github.com/CatVodTVOfficial/TVBoxOSC)
- [takagen99/Box](https://github.com/takagen99/Box) (Updated: 0cfa60f9661479c0066b5f18e99fb134090c1bcd)
- [chengxue2020-org/Box](https://github.com/chengxue2020-org/Box) (Updated: 0cfa60f9661479c0066b5f18e99fb134090c1bcd)




=== Setting Up the Configuration Address ===
- 数据源 > Input Source URL address
- 直播 (Optional) > Input Live URL (http) address. If empty, will take Live URL from Source file
- EPG (Optional) > Input EPG URL (http) address. If empty, will take EPG URL from Source file. If not found in Source file, default from http://epg.51zmt.top:8000/api/diyp/

中文配置地址：
C:\Users\Frank\Documents\GitHub\takagen99\app\src\main\res\values-zh\strings.xml


=== Source Code - 修改默认设置 ===
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
