#include "HelloWorldScene.h"
#include "platform/android/jni/JniHelper.h"
#include <jni.h>

USING_NS_CC;

#define MENU_ITEM_FONT "Umpush-Bold.ttf"

Scene* HelloWorld::createScene()
{
    // 'scene' is an autorelease object
    auto scene = Scene::create();
    
    // 'layer' is an autorelease object
    auto layer = HelloWorld::create();

    // add layer as a child to scene
    scene->addChild(layer);

    // return the scene
    return scene;
}

// on "init" you need to initialize your instance
bool HelloWorld::init()
{
    //////////////////////////////
    // 1. super init first
    if ( !Layer::init() )
    {
        return false;
    }
    
    Size visibleSize = Director::getInstance()->getVisibleSize();
    Vec2 origin = Director::getInstance()->getVisibleOrigin();

    /////////////////////////////
    // 2. add a menu item with "X" image, which is clicked to quit the program
    //    you may modify it.

    // add a "close" icon to exit the progress. it's an autorelease object

    auto menuNode = Node::create();
    auto searchItem = MenuItemFont::create("Search");
    searchItem->setFontNameObj(MENU_ITEM_FONT);
    searchItem->setFontSizeObj(36);
    searchItem->setColor(Color3B(0xFF, 0xFF, 0xFF));
    searchItem->setPosition(origin.x + visibleSize.width/2, origin.y + 4*visibleSize.height/5);

    searchItem->setCallback([](Ref *pSender){
        cocos2d::JniMethodInfo methodInfo;

        if (! cocos2d::JniHelper::getStaticMethodInfo(methodInfo, "org/cocos2dx/cpp/AppActivity", "alertJNI", "()Ljava/lang/Object;")) {
        return;
        }
        jobject activityObj = methodInfo.env->CallStaticObjectMethod(methodInfo.classID, methodInfo.methodID);
        if(!cocos2d::JniHelper::getMethodInfo(methodInfo, "org/cocos2dx/cpp/AppActivity", "openBlueTooth", "()V" )) {
        log("didn't find nostatic method.");
        return;
        }
        methodInfo.env->CallVoidMethod(activityObj, methodInfo.methodID);
        methodInfo.env->DeleteLocalRef(methodInfo.classID);

        });

    auto menu = Menu::create(searchItem, NULL);
    menu->setAnchorPoint(Vec2(0,0));
    menu->setPosition(origin);
    menuNode->addChild(menu);
    this->addChild(menuNode);

//    auto closeItem = MenuItemImage::create(
//                                           "CloseNormal.png",
//                                           "CloseSelected.png",
//                                           CC_CALLBACK_1(HelloWorld::menuCloseCallback, this));
//    
//	closeItem->setPosition(Vec2(origin.x + visibleSize.width - closeItem->getContentSize().width/2 ,
//                                origin.y + closeItem->getContentSize().height/2));
//
//    // create menu, it's an autorelease object
//    auto menu = Menu::create(closeItem, NULL);
//    menu->setPosition(Vec2(visibleSize.width/2, visibleSize.height/2));
//    this->addChild(menu, 1);
//
//    /////////////////////////////
//    // 3. add your codes below...
//
//    // add a label shows "Hello World"
//    // create and initialize a label
//    
//    auto label = Label::createWithTTF("Hello World", "fonts/Marker Felt.ttf", 24);
//    
//    // position the label on the center of the screen
//    label->setPosition(Vec2(origin.x + visibleSize.width/2,
//                            origin.y + visibleSize.height - label->getContentSize().height));
//
//    // add the label as a child to this layer
//    this->addChild(label, 1);
//
//    // add "HelloWorld" splash screen"
//    auto sprite = Sprite::create("HelloWorld.png");
//
//    // position the sprite on the center of the screen
//    sprite->setPosition(Vec2(visibleSize.width/2 + origin.x, visibleSize.height/2 + origin.y));
//
//    // add the sprite as a child to this layer
//    this->addChild(sprite, 0);
//    
    return true;
}


void HelloWorld::menuCloseCallback(Ref* pSender)
{
	//JNIEnv* jniEnv = cocos2d::JniHelper::getEnv();

    cocos2d::JniMethodInfo methodInfo;

      if (! cocos2d::JniHelper::getStaticMethodInfo(methodInfo, "org/cocos2dx/cpp/AppActivity", "alertJNI", "()Ljava/lang/Object;")) {
          return;
      }
      jobject activityObj = methodInfo.env->CallStaticObjectMethod(methodInfo.classID, methodInfo.methodID);
      if(!cocos2d::JniHelper::getMethodInfo(methodInfo, "org/cocos2dx/cpp/AppActivity", "openBlueTooth", "()V" )) {
    	  log("didn't find nostatic method.");
    	  return;
      }
      methodInfo.env->CallVoidMethod(activityObj, methodInfo.methodID);
      methodInfo.env->DeleteLocalRef(methodInfo.classID);


}
