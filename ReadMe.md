# 项目简介
抖音App,部分视频无法直接下载，破解之。

# 开发环境

| 名称    | 版本   |
| :-----------: | :----:|
|Android Studio | 4.0.0  |
|Gradle         | 6.5|
|Android Gradle Plugin| 4.0.0 |

# 实现原理
模拟手机浏览器User-Agent请求抖音分享链接，可通过接口拿到视频地址进行下载。<br>

0. get videoid
https://v.douyin.com/xxxxxx/
分享链接 response code = 302<br>
获取response.header.location，字符串截取id.

1. get api
https://www.iesdouyin.com/web/api/v2/aweme/iteminfo/?item_ids=(上一部获取的id)

2. get video information
由上一步获取response body 交由Gson处理，即可获得下载视频的链接

# License

Copyright 2022 Daiyuan Studio<br>
Licensed under the Apache License, Version 2.0 (the "License");<br>
you may not use this file except in compliance with the License.<br>
   You may obtain a copy of the License at
<br>
       http://www.apache.org/licenses/LICENSE-2.0
<br>
   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
