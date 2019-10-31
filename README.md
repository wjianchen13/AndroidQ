# android Q相关特性

android Q需要做适配，这是适配涉及笔记，这里记录一下。

## 分区存储
Android Q 在外部存储设备中为每个应用提供了一个“隔离存储沙盒”（例如 /sdcard）。任何其他应用都无法直接访问您  
应用的沙盒文件。由于文件是您应用的私有文件，因此您不再需要任何权限即可在外部存储设备中访问和保存自己的文件。  
此变更可让您更轻松地保证用户文件的隐私性，并有助于减少应用所需的权限数量。  

## 权限
Android Q 更改了应用对设备外部存储设备中的文件（如：/sdcard ）的访问方式。继续使用 READ_EXTERNAL_STORAGE   
和 WRITE_EXTERNAL_STORAGE 权限，只不过当拥有这些权限的时候，你只能访问媒体文件，无法访问其他文件。  
在早先的beta版本中，Android需要申请特定的媒体权限 :READ_MEDIA_IMAGES, READ_MEDIA_VIDEO , READ_MEDIA_AUDIO,  
但是在beta4中，这些权限被废弃。  

## 访问沙盒文件
使用getExternalFilesDir()获取路径，然后对该路径下的文件进行读写操作，由于文件是私有的，所以不需要申请权限。   
该目录下的文件会随app卸载而清除。  
```Java
String filePath = getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS).getAbsolutePath();  
```

## 访问MediaStore文件
MediaStore 下保存的文件不会随app卸载而清除，但是需要申请相关文件读写权限，READ_EXTERNAL_STORAGE和WRITE_EXTERNAL_STORAGE.  
这两个权限是一组的，申请其中一个会自动赋予另外一个权限。
访问MediaStore目录：
```Java
String filePath = getExternalFilesDir(Environment.DIRECTORY_PICTURES).getAbsolutePath();
```
当然还有其他类型，DIRECTORY_MOVIES，DIRECTORY_MUSIC等等。

## 参考文档
https://blog.csdn.net/honjane/article/details/94288585
https://juejin.im/post/5cad5b7ce51d456e5a0728b0

## license

   Copyright 2019 wjianchen13

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.




