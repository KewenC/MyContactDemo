package com.kewenc.mycontactdemo;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity{
    private String[] columns = { ContactsContract.Contacts._ID,// 获得ID值
            ContactsContract.Contacts.DISPLAY_NAME,// 获得姓名
            ContactsContract.CommonDataKinds.Phone.NUMBER,// 获得电话
            ContactsContract.CommonDataKinds.Phone.CONTACT_ID, };
    TextView tv;
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tv = (TextView) findViewById(R.id.result);// 获得布局文件中的标签
        tv.setText(getQueryData());// 为标签设置数据
    }

    private String getQueryData() {
        StringBuilder sb = new StringBuilder();// 用于保存字符串
        ContentResolver resolver = getContentResolver();// 获得ContentResolver对象
        Cursor cursor = resolver.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);// 查询记录
        while (cursor.moveToNext()) {
            int idIndex = cursor.getColumnIndex(columns[0]);// 获得ID值的索引
            int displayNameIndex = cursor.getColumnIndex(columns[1]);// 获得姓名索引
            int id = cursor.getInt(idIndex);// 获得id
            String displayName = cursor.getString(displayNameIndex);// 获得名称
            Cursor phone = resolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, columns[3] + "=" + id, null, null);
            while (phone.moveToNext()) {
                int phoneNumberIndex = phone.getColumnIndex(columns[2]);// 获得电话索引
                String phoneNumber = phone.getString(phoneNumberIndex);// 获得电话
                sb.append(displayName + ": " + phoneNumber + "\n");// 保存数据
            }
        }
        cursor.close();// 关闭游标
        return sb.toString();
    }
    //Button
    public void ContactBtn (View v){
        Intent intent = new Intent(Intent.ACTION_PICK,ContactsContract.Contacts.CONTENT_URI);
        startActivityForResult(intent, 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){
            case 0:
                if(data==null) { return; }
                //处理返回的data,获取选择的联系人信息
                Uri uri=data.getData();
                String[] contacts=getPhoneContacts(uri);
                tv.setText("联系人="+contacts[0]+"电话："+contacts[1]);
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
    private String[] getPhoneContacts(Uri uri){
        String[] contact=new String[2];
        //得到ContentResolver对象
        ContentResolver cr = getContentResolver();
        //取得电话本中开始一项的光标
        Cursor cursor=cr.query(uri,null,null,null,null);
        if(cursor!=null) {
            cursor.moveToFirst();
            //取得联系人姓名
            int nameFieldColumnIndex=cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);
            contact[0]=cursor.getString(nameFieldColumnIndex);
            //取得电话号码
//            int ContactId = cursor.getInt(cursor.getColumnIndex(ContactsContract.Contacts._ID));
//            Cursor phone = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.Contacts._ID+ "=" + ContactId, null, null);
            String ContactId = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
            Cursor phone = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=" + ContactId, null, null);
            if(phone != null){
                phone.moveToFirst();
                int aaa = phone.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                contact[1] = phone.getString(aaa);
                phone.close();
            }
            cursor.close();
        } else {
            return null;
        }
        return contact;
    }
}