package com.bilibili.lingxiao.home.find.ui

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import com.bilibili.lingxiao.R
import com.bilibili.lingxiao.home.find.RankListView
import com.bilibili.lingxiao.home.find.model.RankListData
import com.bilibili.lingxiao.home.find.presenter.RankListPresenter
import com.bilibili.lingxiao.utils.ToastUtil
import com.camera.lingxiao.common.app.BaseFragment
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.facebook.drawee.view.SimpleDraweeView
import kotlinx.android.synthetic.main.normal_refresh_view.*
import kotlinx.android.synthetic.main.normal_refresh_view.view.*
import kotlinx.android.synthetic.main.normal_refresh_view.view.refresh

class RankListFragment :BaseFragment(),RankListView{
    private var mRankList = arrayListOf<RankListData.Item>()
    private lateinit var mPresenter: RankListPresenter
    private lateinit var mRankListAdapter :RankListAdapter
    private var page = 1
    private val pageSize = 20
    private var pageType = ""
    override val contentLayoutId: Int
        get() = R.layout.normal_refresh_view

    override fun initArgs(bundle: Bundle?) {
        super.initArgs(bundle)
        pageType = bundle!!.getString("type")
    }

    override fun initWidget(root: View) {
        super.initWidget(root)
        mPresenter = RankListPresenter(this,this)
        mRankListAdapter = RankListAdapter(R.layout.item_rank_list, mRankList)
        root.recycerView.layoutManager = LinearLayoutManager(context)
        root.recycerView.adapter = mRankListAdapter
        root.refresh.setOnRefreshListener {
            mRankList.clear()
            mPresenter.getOriginRankingList(pageType,page,pageSize)
        }
        root.refresh.setOnLoadMoreListener{
            page++
            mPresenter.getOriginRankingList(pageType,page,pageSize)
        }
    }

    override fun onFirstVisiblity() {
        super.onFirstVisiblity()
        refresh.autoRefresh()
    }
    override fun onGetRankList(data: MutableList<RankListData.Item>) {
        mRankListAdapter.addData(data)
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

    inner class RankListAdapter(layout:Int,data: MutableList<RankListData.Item>?) :
        BaseQuickAdapter<RankListData.Item, BaseViewHolder>(layout,data) {
        override fun convert(helper: BaseViewHolder, item: RankListData.Item) {
            helper.setText(R.id.title,item.title)
            helper.setText(R.id.up_name,item.name)
            helper.setText(R.id.grade,"综合评分：${item.pts}")
            var imageCover = helper.getView<SimpleDraweeView>(R.id.cover_image)
            imageCover.setImageURI(Uri.parse(item.cover))
            var showAllVideo = helper.getView<TextView>(R.id.text_show_all)
            if (item.children != null)
                showAllVideo.visibility = View.VISIBLE
            else
                showAllVideo.visibility = View.GONE

            helper.setText(R.id.text_rank,"${helper.position+1}")
            if (helper.position+1 > 3)
                helper.setTextColor(R.id.text_rank,resources.getColor(R.color.black_alpha_144))
            else
                helper.setTextColor(R.id.text_rank,resources.getColor(R.color.colorPrimary))

            var parent = helper.getView<LinearLayout>(R.id.root_show_all)
            showAllVideo.setOnClickListener {
                it.visibility = View.GONE
                item.children!!.forEach {
                    addChildItem(parent,context!!,it)
                }
            }
        }

        fun addChildItem(parent:ViewGroup,context:Context, data :RankListData.Item.Children){
            var titleTextView = TextView(context)
            titleTextView.text = data.title
            titleTextView.setTextColor(context.resources.getColor(R.color.black_alpha_176))
            titleTextView.textSize = 14f


            var messageTextView = TextView(context)
            messageTextView.text = "综合评分：${data.pts}"
            messageTextView.setTextColor(context.resources.getColor(R.color.black_alpha_144))
            messageTextView.textSize = 12f

            parent.addView(titleTextView)
            parent.addView(messageTextView)
        }
    }
}