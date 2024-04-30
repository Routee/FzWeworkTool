### Doc
- GET 检查更新 [AppUpdate](app/src/main/java/com/functorz/worktool/model/AppUpdate.java) 
  > hosts/appUpdate/checkUpdate 检查hosts更新
  > https://worktool.asrtts.cn/appUpdate/checkUpdate 检查官方更新


- GET 获取机器人配置 [MyConfigBean](app/src/main/java/com/functorz/worktool/model/MyConfigBean.kt)
  > /robot/robotInfo/get?robotId=$robotId

- POST 推送本地文件
  > /fileUpload/upload?robotId=$robotId

- POST 更新机器人 如设置页设置机器人“回复策略”，高级选项页设置消息回调
  ```
    int openCallback: 0/1
    String callbackUrl
    String robotId
    int replyAll //replyStrategy=replyAll+1 replyStrategy=0不回复 replyStrategy=1回复at replyStrategy=2回复所有 replyAll为后端保存，replyStrategy为前端使用
  ```
  > /robot/robotInfo/update?robotId=$robotId

- [WeworkMessageListBean](app/src/main/java/com/functorz/worktool/model/WeworkMessageListBean.kt)
- [WeworkMessageBean](app/src/main/java/com/functorz/worktool/model/WeworkMessageBean.java)

