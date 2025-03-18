package com.example.autoanswer;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.content.Context;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class AutoAnswerService extends AccessibilityService {
    private static final String TAG = "AutoAnswerService";
    private Random random = new Random();

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        AccessibilityNodeInfo rootNode = getRootInActiveWindow();
        if (rootNode == null) return;

        try {
            // 检测Step1：答题窗口
            if (hasNodeWithText(rootNode, "多选题")||hasNodeWithText(rootNode, "单选题")) {
                Log.d(TAG, "检测到答题窗口");
                Toast.makeText(this, "检测到答题窗口", Toast.LENGTH_SHORT).show();
                
                // 直接查找并点击选项按钮
                List<AccessibilityNodeInfo> optionButtons = new ArrayList<>();
                findClickableOptionButtons(rootNode, optionButtons);
                
                if (!optionButtons.isEmpty()) {
                    // 随机选择一个选项
                    int randomIndex = random.nextInt(optionButtons.size());
                    AccessibilityNodeInfo option = optionButtons.get(randomIndex);
                    
                    // 执行点击操作
                    if (option.isClickable()) {
                        option.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                        Log.d(TAG, "点击了选项");
                        Toast.makeText(this, "点击了选项，3秒后点击提交", Toast.LENGTH_SHORT).show();
                    }
                    
                    // 延迟3秒后点击提交按钮
                    new android.os.Handler().postDelayed(
                        new Runnable() {
                            public void run() {
                                AccessibilityNodeInfo root = getRootInActiveWindow();
                                if (root != null) {
                                    clickNodeWithText(root, "提交");
                                    Toast.makeText(AutoAnswerService.this, "点击了提交，3秒后点击确定", Toast.LENGTH_SHORT).show();
                                    
                                    // 延迟3秒后点击确定按钮
                                    new android.os.Handler().postDelayed(
                                        new Runnable() {
                                            public void run() {
                                                AccessibilityNodeInfo root = getRootInActiveWindow();
                                                if (root != null) {
                                                    clickNodeWithText(root, "确定");
                                                    Toast.makeText(AutoAnswerService.this, "点击了确定", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        },
                                    3000); // 3秒延时
                                }
                            }
                        }, 
                    3000); // 3秒延时
                } else {
                    Log.d(TAG, "未找到可点击的选项按钮");
                    Toast.makeText(this, "未找到可点击的选项按钮", Toast.LENGTH_SHORT).show();
                }
            }
            
            // 检测Step2：答案解析窗口
            else if (hasNodeWithText(rootNode, "答案") || hasNodeWithText(rootNode, "解析") || 
                     hasNodeWithText(rootNode, "正确答案")) {
                Log.d(TAG, "检测到答案解析窗口");
                Toast.makeText(this, "检测到答案解析窗口，3秒后点击确定", Toast.LENGTH_SHORT).show();
                
                // 延迟3秒后点击确定按钮
                new android.os.Handler().postDelayed(
                    new Runnable() {
                        public void run() {
                            AccessibilityNodeInfo root = getRootInActiveWindow();
                            if (root != null) {
                                clickNodeWithText(root, "确定");
                                Toast.makeText(AutoAnswerService.this, "点击了确定", Toast.LENGTH_SHORT).show();
                            }
                        }
                    },
                3000); // 3秒延时
            }
        } catch (Exception e) {
            Log.e(TAG, "处理事件时出错: " + e.getMessage());
            Toast.makeText(this, "处理事件时出错: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        } finally {
            rootNode.recycle();
        }
    }

    @Override
    public void onInterrupt() {
        Log.d(TAG, "服务中断");
    }

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        Log.d(TAG, "服务已连接");
        Toast.makeText(this, "自动答题服务已启动", Toast.LENGTH_SHORT).show();
        
        AccessibilityServiceInfo info = new AccessibilityServiceInfo();
        info.eventTypes = AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED | 
                          AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED;
        info.feedbackType = AccessibilityServiceInfo.FEEDBACK_GENERIC;
        info.notificationTimeout = 100;
        info.flags = AccessibilityServiceInfo.FLAG_REPORT_VIEW_IDS |
                    AccessibilityServiceInfo.FLAG_RETRIEVE_INTERACTIVE_WINDOWS;
        
        setServiceInfo(info);
    }

    // 辅助方法：检查是否存在包含指定文本的节点
    private boolean hasNodeWithText(AccessibilityNodeInfo root, String text) {
        if (root == null || text == null) return false;
        List<AccessibilityNodeInfo> nodes = root.findAccessibilityNodeInfosByText(text);
        return nodes != null && !nodes.isEmpty();
    }

    // 辅助方法：点击包含指定文本的节点
    private void clickNodeWithText(AccessibilityNodeInfo root, String text) {
        if (root == null || text == null) return;
        List<AccessibilityNodeInfo> nodes = root.findAccessibilityNodeInfosByText(text);
        if (nodes != null && !nodes.isEmpty()) {
            for (AccessibilityNodeInfo node : nodes) {
                if (node.isClickable()) {
                    node.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                    Log.d(TAG, "点击了按钮: " + text);
                    return;
                }
                
                // 如果节点本身不可点击，尝试查找其可点击的父节点
                AccessibilityNodeInfo parent = node;
                while (parent != null) {
                    if (parent.isClickable()) {
                        parent.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                        Log.d(TAG, "点击了按钮的父节点: " + text);
                        return;
                    }
                    parent = parent.getParent();
                }
            }
        }
    }
    
    // 辅助方法：查找包含指定文本之一的节点
    private List<AccessibilityNodeInfo> findNodesContainingTexts(AccessibilityNodeInfo root, String[] texts) {
        List<AccessibilityNodeInfo> allNodes = new ArrayList<>();
        for (String text : texts) {
            List<AccessibilityNodeInfo> nodes = root.findAccessibilityNodeInfosByText(text);
            if (nodes != null && !nodes.isEmpty()) {
                allNodes.addAll(nodes);
            } else {
                Log.d(TAG, "未找到包含文本的节点: " + text);
            }
        }
        return allNodes.isEmpty() ? null : allNodes;
    }

    // 递归查找可点击的选项按钮
    private void findClickableOptionButtons(AccessibilityNodeInfo node, List<AccessibilityNodeInfo> results) {
        if (node == null) return;

        // 检查节点是否可点击
        if (node.isClickable()) {
            CharSequence text = node.getText();
            if (text != null) {
                String textStr = text.toString().trim();
                // 精确匹配A、B、C、D选项
                if (textStr.equals("A") || textStr.equals("B") || 
                    textStr.equals("C") || textStr.equals("D") ||
                    textStr.equals("劳动资料") || textStr.equals("劳动对象") ||
                    textStr.equals("劳动者") || textStr.equals("科学技术")) {
                    results.add(node);
                    Log.d(TAG, "找到可点击选项: " + textStr);
                    Toast.makeText(this, "找到可点击选项: " + textStr, Toast.LENGTH_SHORT).show();
                }
            }
        }

        // 如果当前节点不可点击，但包含目标文本，尝试查找其可点击的父节点
        if (!node.isClickable()) {
            CharSequence text = node.getText();
            if (text != null) {
                String textStr = text.toString().trim();
                if (textStr.equals("A") || textStr.equals("B") || 
                    textStr.equals("C") || textStr.equals("D") ||
                    textStr.equals("劳动资料") || textStr.equals("劳动对象") ||
                    textStr.equals("劳动者") || textStr.equals("科学技术")) {
                    // 查找可点击的父节点
                    AccessibilityNodeInfo parent = node.getParent();
                    while (parent != null) {
                        if (parent.isClickable()) {
                            results.add(parent);
                            Log.d(TAG, "找到可点击选项的父节点: " + textStr);
                            Toast.makeText(this, "找到可点击选项的父节点: " + textStr, Toast.LENGTH_SHORT).show();
                            break;
                        }
                        AccessibilityNodeInfo temp = parent;
                        parent = parent.getParent();
                        if (temp != node) {
                            temp.recycle();
                        }
                    }
                }
            }
        }

        // 递归查找子节点
        for (int i = 0; i < node.getChildCount(); i++) {
            AccessibilityNodeInfo child = node.getChild(i);
            if (child != null) {
                findClickableOptionButtons(child, results);
                child.recycle();
            }
        }
    }

    // 检查服务是否已启用
    public static boolean isServiceEnabled(Context context) {
        int accessibilityEnabled = 0;
        final String service = "com.example.autoanswer/com.example.autoanswer.AutoAnswerService";
        try {
            accessibilityEnabled = Settings.Secure.getInt(
                    context.getApplicationContext().getContentResolver(),
                    Settings.Secure.ACCESSIBILITY_ENABLED);
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }
        
        if (accessibilityEnabled == 1) {
            String settingValue = Settings.Secure.getString(
                    context.getApplicationContext().getContentResolver(),
                    Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);
                    
            if (settingValue != null) {
                return settingValue.contains(service);
            }
        }
        return false;
    }
} 