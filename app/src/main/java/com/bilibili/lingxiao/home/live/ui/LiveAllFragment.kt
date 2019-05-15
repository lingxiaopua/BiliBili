package com.bilibili.lingxiao.home.live.ui


import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.view.View
import com.bilibili.lingxiao.R
import com.bilibili.lingxiao.home.live.model.LiveAllData
import com.bilibili.lingxiao.home.live.model.LiveData
import com.bilibili.lingxiao.home.live.presenter.LiveAllPresenter
import com.bilibili.lingxiao.home.live.view.LiveAllView
import com.bilibili.lingxiao.utils.StringUtil
import com.bilibili.lingxiao.utils.ToastUtil
import com.camera.lingxiao.common.app.BaseFragment
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.facebook.drawee.view.SimpleDraweeView
import kotlinx.android.synthetic.main.normal_refresh_view.*
import kotlinx.android.synthetic.main.normal_refresh_view.view.*
import org.greenrobot.eventbus.EventBus

class LiveAllFragment :BaseFragment(),LiveAllView{
    private var liveList = arrayListOf<LiveAllData.LiveInfo>()
    lateinit var videoAdapter:VideoAdapter
    private var presenter = LiveAllPresenter(this,this)
    var hot = false
    var page = 1
    override val contentLayoutId: Int
        get() = R.layout.normal_refresh_view

    override fun initArgs(bundle: Bundle?) {
        super.initArgs(bundle)
        bundle?.let {
            hot = it.getBoolean("hot")
        }
    }
    override fun initWidget(root: View) {
        super.initWidget(root)
        videoAdapter =
            VideoAdapter(R.layout.item_live_video, liveList)
        var manager = GridLayoutManager(context,2)
        root.recycerView.adapter = videoAdapter
        root.recycerView.layoutManager = manager
        root.refresh.setOnRefreshListener {
            page = 1
            liveList.clear()
            if (hot){
                presenter.getLiveHotList(1)
            }else{
                presenter.getLiveNewList(1)
            }
        }
        root.refresh.setOnLoadMoreListener {
            page++
            if (hot){
                presenter.getLiveHotList(page)
            }else{
                presenter.getLiveNewList(page)
            }
        }
        videoAdapter.setOnItemClickListener { adapter, view, position ->
            val intent = Intent(context, LivePlayActivity::class.java)
            intent.putExtra("play_url",liveList[position].playUrl)
            intent.putExtra("room_id",liveList[position].roomid)
            startActivity(intent)
            var live = LiveData.RecommendDataBean.LivesBean()
            live.owner = LiveData.RecommendDataBean.LivesBean.OwnerBean()
            live.owner.face = liveList[position].face
            live.owner.name = liveList[position].uname
            live.online = liveList[position].online
            live.area = liveList[position].areaName
            live.room_id = liveList[position].roomid
            EventBus.getDefault().postSticky(live)
        }
    }

    override fun onFirstVisiblity() {
        super.onFirstVisiblity()
        refresh.autoRefresh()
    }
    override fun onGetHotList(data: LiveAllData) {
        videoAdapter.addData(data.list)
        refresh.finishRefresh()
        refresh.finishLoadMore()
    }

    override fun onGetNewList(data: LiveAllData) {
        videoAdapter.addData(data.list)
        refresh.finishRefresh()
        refresh.finishLoadMore()
    }

    override fun showDialog() {

    }

    override fun diamissDialog() {

    }

    override fun showToast(text: String?) {
        ToastUtil.show(text)
    }

    class VideoAdapter(layoutResId: Int, data: MutableList<LiveAllData.LiveInfo>) :
        BaseQuickAdapter<LiveAllData.LiveInfo, BaseViewHolder>(layoutResId, data) {
        override fun convert(helper: BaseViewHolder, item: LiveAllData.LiveInfo) {
            var image : SimpleDraweeView = helper.getView(R.id.live_user_image)
            image.setImageURI(Uri.parse(item.systemCover))
            helper.setText(R.id.live_title,item.title)
            helper.setText(R.id.live_category_name,item.areaName)
            helper.setText(R.id.live_username,item.uname)
            helper.setText(R.id.live_people_number, StringUtil.getBigDecimalNumber(item.online))
        }
    }
}