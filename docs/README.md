# Android App for LHE 有料会員向けアプリ 

- 開発にあたり、参考にした情報をまとめます。


--- 

## push通知

### firebase 
[https://firebase.google.com/docs/cloud-messaging/](https://firebase.google.com/docs/cloud-messaging/)

### GCM client 
[https://developers.google.com/cloud-messaging/android/client](https://developers.google.com/cloud-messaging/android/client)

- sample  
[https://github.com/googlesamples/google-services](https://github.com/googlesamples/google-services)



### push通知を送信してみる

- url 

		https://fcm.googleapis.com/fcm/send

- headers 

		Authorization:key=[SenderIDと紐付く"Server key (legacy token)]
		Content-Type:application/json


- parameter(json) 

		{
		  "to": "[FirebaseInstanceId.getInstance().getToken()で生成されたToken]",
		  "data": {
		    "hello": "This is a Firebase Cloud Messaging Device Group Message!",
		   }
		}


---

## 実行時に以下のExceptionが出た場合

		Error:Execution failed for task ':app:transformClassesWithDexForDebug'.
		> com.android.build.api.transform.TransformException: com.android.ide.common.process.ProcessException: java.util.concurrent.ExecutionException: com.android.dex.DexIndexOverflowException: method ID not in [0, 0xffff]: 65536

### 対処法
[https://developer.android.com/studio/build/multidex.html](https://developer.android.com/studio/build/multidex.html)


--- 
## UI

### Bottom navigation
[https://material.google.com/components/bottom-navigation.html#](https://material.google.com/components/bottom-navigation.html#)

--- 
### フォントサイズ

		Androidのガイドラインでは 18sp が標準フォントサイズ

[https://material.google.com/style/typography.html](https://material.google.com/style/typography.html)

- Text Size Micro（極小） 12sp 
- Text Size Small（小）：14sp 
- Text Size Medium（標準）：18sp 
- Text Size Large（第）： 22sp 

--- 
### リソースの書き出し手順
1. psdの画像を、w1080pxに拡大する（xxhdpiのサイズ）
2. 0.5x/0.75x/0.25x/2x/1x のバリエーションで書き出しする


--- 
### SSLのオレオレ証明書を許可する
[SSLのオレオレ証明書を許可する](http://qiita.com/muran001/items/ddbd0341670fb03dce05)
