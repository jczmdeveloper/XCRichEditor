package com.czm.xcricheditor;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.czm.xcricheditor.util.PhoneUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by caizhiming on 2016/10/21.
 * 仿兴趣部落的一个图文混排编辑器控件
 */

public class XCRichEditor extends RelativeLayout{
    public static final String PATTERN = "(<img src=\"[^\"]*\"\\s*/>)";
    private View mRoot;
    private Context mContext;
    private List<EditItem> mDatas;
    private RecyclerView mRecyclerView;
    private LinearLayoutManager mFullyLinearLayoutManager;
    private XCRichEditorAdapter mAdapter;

    private int lastPosition = -1;
    private EditText lastEditText;

    public XCRichEditor(Context context) {
        this(context, null);
    }
    public XCRichEditor(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }
    public XCRichEditor(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context,attrs,defStyleAttr);
    }

    private void initView(Context context, AttributeSet attrs, int defStyleAttr) {
        mContext = context;
        mRoot = View.inflate(mContext, R.layout.layout_rich_editor, this);
        mRecyclerView = (RecyclerView) mRoot.findViewById(R.id.id_edit_component);
        mRecyclerView.addItemDecoration(new SpaceItemDecoration(PhoneUtil.dip2px(mContext,10)));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mFullyLinearLayoutManager = new LinearLayoutManager(mContext);
        mAdapter = new XCRichEditorAdapter(mContext);
        mAdapter.setComponentAdapterListener(new XCRichEditorAdapter.ComponentAdapterListener() {
            @Override
            public void change(int position, EditText editText) {
                lastEditText = editText;
                lastPosition = position;
            }

            @Override
            public void delete(int position) {
                if(position == mAdapter.getItemCount()-1){
                    //最后一个
                    mDatas.remove(position);
                    mAdapter.notifyDataSetChanged();
                }else if(position > 0){
                    String str = "";
                    if (position > 0 && mDatas.get(position - 1).getType() == 0) {
                        str = mDatas.get(position - 1).getContent();
                    }
                    if (position < mDatas.size() - 1 && mDatas.get(position + 1).getType() == 0) {
                        str += mDatas.get(position + 1).getContent();
                    }
                    mDatas.remove(position + 1);
                    mDatas.remove(position);
                    mDatas.remove(position - 1);
                    EditItem textData = new EditItem();
                    textData.setType(0);
                    textData.setContent(str);
                    mDatas.add(position - 1, textData);
                    mAdapter.notifyDataSetChanged();
                }
                Log.e("czm","rich text="+getRichText());
            }
        });
        mRecyclerView.setLayoutManager(mFullyLinearLayoutManager);
        mRecyclerView.setAdapter(mAdapter);
        if (null == mDatas) {
            mDatas = new ArrayList<>();
        }
        EditItem data = new EditItem();
        data.setType(0);
        data.setContent("");
        mDatas.add(data);
        mAdapter.setData(mDatas);
    }
    public String getRichText(){
        String content = "";
        if (null != mDatas && mDatas.size() > 0) {
            for (EditItem item : mDatas) {
                if (item.getType() == 0) {
                    content += item.getContent();
                } else if (item.getType() == 1) {
                    String pathTag = "<img src=\"" + item.getContent() + "\"/>";
                    content += pathTag;
                }
            }
        }
        return content;

    }
    public void addImage(List<EditItem> info) {
        addData(info);
    }
    public void addImage(EditItem item){
        List<EditItem> datas = new ArrayList<EditItem>();
        datas.add(item);
        addData(datas);
    }
    public void setRichText(String content) {
        if (mDatas != null && mDatas.size() > 0) {
            mDatas.clear();
        }
        Pattern pattern = Pattern.compile(PATTERN);
        Matcher matcher = pattern.matcher(content);
        while (matcher.find()) {
            String url = matcher.group();
            StringBuffer sb = new StringBuffer();
            matcher.appendReplacement(sb, "");
            EditItem item = new EditItem();
            item.setType(0);
            item.setContent(sb.toString().trim());
            mDatas.add(item);
            if (url.contains("<img")) {
                final String path = url.substring(12, url.length() - 3);
                EditItem imgItem = new EditItem();
                imgItem.setType(1);
                imgItem.setUri(Uri.parse("file://" + path));
                imgItem.setContent(path);
                mDatas.add(imgItem);
            }

        }

        StringBuffer sb = new StringBuffer();
        matcher.appendTail(sb);
        EditItem textItem = new EditItem();
        textItem.setType(0);
        textItem.setContent(sb.toString().trim());
        mDatas.add(textItem);
        mAdapter.setData(mDatas);
    }
    private void addData(List<EditItem> info) {
        if (lastPosition >= 0 && null != lastEditText && !TextUtils.isEmpty(lastEditText.getText().toString().trim())) {
            String lastEditStr = lastEditText.getText().toString();
            int cursorIndex = lastEditText.getSelectionStart();
            String editStr1 = lastEditStr.substring(0, cursorIndex).trim();
            String editStr2 = lastEditStr.substring(cursorIndex).trim();
            boolean ispre = false;
            boolean ispost = false;
            if (lastPosition > 0 && mDatas.get((lastPosition - 1)).getType() == 0) {//pre item is text
                editStr1 = mDatas.get((lastPosition - 1)).getContent() + editStr1;
                ispre = true;
            }
            if (lastPosition < mDatas.size() - 1 && mDatas.get((lastPosition + 1)).getType() == 0) {//post item is text
                editStr2 = editStr2 + mDatas.get((lastPosition + 1)).getContent();
                ispost = true;
            }
            int tmpPosition = lastPosition;
            if (ispre && ispost) {
                mDatas.remove(lastPosition + 1);
                mDatas.remove(lastPosition);
                mDatas.remove(lastPosition - 1);
                --tmpPosition;
            } else if (ispre) {
                mDatas.remove(lastPosition);
                mDatas.remove(lastPosition - 1);
                --tmpPosition;
            } else if (ispost) {
                mDatas.remove(lastPosition + 1);
                mDatas.remove(lastPosition);
            } else {
                mDatas.remove(lastPosition);
            }
            EditItem preData = new EditItem();
            preData.setType(0);
            preData.setContent(editStr1);
            mDatas.add(tmpPosition++, preData);
            for (int i = 0; i < info.size(); i++) {
                mDatas.add(tmpPosition++, info.get(i));
                if (i == info.size() - 1 && !TextUtils.isEmpty(editStr2)) {
                    EditItem postData = new EditItem();
                    postData.setType(0);
                    postData.setContent(editStr2);
                    mDatas.add(tmpPosition++, postData);
                } else {
                    EditItem postData = new EditItem();
                    postData.setType(0);
                    postData.setContent("");
                    mDatas.add(tmpPosition++, postData);
                }
            }
        } else {
            for (int i = 0; i < info.size(); i++) {
                mDatas.add(info.get(i));
                EditItem textData = new EditItem();
                textData.setType(0);
                textData.setContent("");
                mDatas.add(textData);
            }
        }
        mAdapter.setData(mDatas);

        Log.e("czm","rich text="+getRichText());

    }
}
