package com.example.localalbum.common;

/**
 * @Description: 界面跳转时 传递的key 命名方式为:来源Activity_目标Activity_KEY名称
 * @author caizhiming
 * @date 2015-3-24
 */
public class ExtraKey {

	public static final String USERINFO_EDIT_TITLE="title";//标题
	public static final String USERINFO_EDIT_VALUE="value";//内容
	public static final String USER_PROPERTYKEY="key";//在property文件中得key
	public static final String USERINFO_EDIT_NUMBER="number";//输入格式是否位数字
	public static final String USERINFO_MAX_LENGTH="max";//输入最大长度
	public static final String EDIT_USERINFO_VALUE="value";//修改后返回的值
	public static final String SELECTINTEREST_USERINFO_VALUE="interest";//选择爱好后返回的值
	public static final String PROVINCE_CITY_NAME="province";//当前选择的省份
	public static final String CLUB_CLUBINFO_ID="clubid";//当前选择的俱乐部ID
	public static final String STATUS="status";//状态码，不同状态代表不同请求.0:关注 1：粉丝 2：想去 3：已报名
	public static final String MAIN_POSITION="position";//主界面当前标签位置，主要用以登录时切换到“我的”界面
	public static final String USERINFO_SIGNTURE = "signture";

	public static final String BROWSER_TITLE="browser_title";//浏览器标题
	public static final String BROWSER_URL="browser_url";//浏览器链接

	public static final String ACTIVITY_ID="activity_id";//活动ID
	public static final String CLUB_ID="club_id";//俱乐部ID

	public static final String FROM_ACTIVITY="from_activity";//在地区选择界面使用，标记是否从activity界面进入
    public static final String URL_ARRAYLIST="url_arraylist";//图片查看界面的url数组
    public static final String URL_ARRAYLIST_ACTIVE="url_arraylist_active";//当前选择的图片

    public static final String ACTIVITY_TYPE="activity_type";//活动类型：想去或带领的活动
    public static final String ACTIVITY_STATUS="activity_status";//活动状态：进行中或已结束

    public static final String DANGKR_TYPE="dangkr_type";//荡客界面跳转到列表界面时的请求类型
    public static final String DANGKR_TITLE="dangkr_title";//荡客界面跳转到列表界面时的标题
    public static final String HOME_PAGE_ID="home_page_id";//跳转到个人主页传递的人物ID
    public static final String HOME_PAGE_IS_LEADER="home_page_is_leader";//个人主页是否领队

    public static final String USER_ID="user_id";//跳转到动态界面的人物ID
    public static final String USER_NAME="user_name";//跳转到动态界面传递的人物名称
	public static final String LOCAL_FOLDER_NAME="local_folder_name";//跳转到相册页的文件夹名称

	public static final String DYNAMIC_ID_AND_DATE="dynamic_id_and_date";//动态ID和时间戳字符串
}
