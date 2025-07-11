//
// ProfileNewHeaderView.swift
// iosApp
//
// Created by abujj on 1/14/25.
// Copyright © 2025 orgName. All rights reserved.
//
import Generated
import JXSegmentedView
import Kingfisher
import MarkdownUI
import MJRefresh
import os.log
import shared
import SwiftUI
import UIKit

// 头部视图
class ProfileNewHeaderView: UIView {
    private var state: ProfileNewState?
    var theme: FlareTheme?

    // 添加关注按钮回调
    var onFollowClick: ((UiRelation) -> Void)?

    // 防重复设置事件的标志
    private var hasSetupEvents = false

    private let bannerImageView: UIImageView = {
        let imageView = UIImageView()
        imageView.contentMode = .scaleAspectFill
        imageView.clipsToBounds = true
        return imageView
    }()

    private let blurEffectView: UIVisualEffectView = {
        let blurEffect = UIBlurEffect(style: .light)
        let view = UIVisualEffectView(effect: blurEffect)
        view.alpha = 0 // 初始时不模糊
        return view
    }()

    private let avatarView: UIImageView = {
        let imageView = UIImageView()
        // imageView.backgroundColor = .gray.withAlphaComponent(0.3)
        imageView.layer.cornerRadius = 40
        imageView.clipsToBounds = true
        imageView.contentMode = .scaleAspectFill
        return imageView
    }()

    private let followButton: UIButton = {
        let button = UIButton(type: .system)
        button.setTitle("follow", for: .normal)
        button.setTitleColor(.white, for: .normal)
        // button.backgroundColor = .systemBlue
        button.layer.cornerRadius = 15
        return button
    }()

    private let nameLabel: UILabel = {
        let label = UILabel()
        label.font = .boldSystemFont(ofSize: 20)
        return label
    }()

    private let handleLabel: UILabel = {
        let label = UILabel()
        label.textColor = .gray
        label.font = .systemFont(ofSize: 15)
        return label
    }()

    private let descriptionLabel: UILabel = {
        let label = UILabel()
        label.numberOfLines = 0
        label.font = .systemFont(ofSize: 15)
        return label
    }()

    private let followsCountLabel: UILabel = {
        let label = UILabel()
        label.font = .systemFont(ofSize: 14)
        label.textColor = .gray
        return label
    }()

    private let fansCountLabel: UILabel = {
        let label = UILabel()
        label.font = .systemFont(ofSize: 14)
        label.textColor = .gray
        return label
    }()

    private let markStackView: UIStackView = {
        let stackView = UIStackView()
        stackView.axis = .horizontal
        stackView.spacing = 4
        stackView.alignment = .center
        return stackView
    }()

    var onFollowsCountTap: (() -> Void)?
    var onFansCountTap: (() -> Void)?
    var onAvatarTap: (() -> Void)?
    var onBannerTap: (() -> Void)?

    // 添加主题观察者
    private var themeObserver: NSObjectProtocol?

    // 添加 userInfo 属性
    private var userInfo: ProfileUserInfo?

    override init(frame: CGRect) {
        super.init(frame: frame)
        setupUI()
        setupThemeObserver()
    }

    @available(*, unavailable)
    required init?(coder _: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }

    deinit {
        // 移除主题观察者
        if let themeObserver {
            NotificationCenter.default.removeObserver(themeObserver)
        }
    }

    private func setupUI() {
        //  backgroundColor = .systemBackground

        // Banner with tap gesture
        addSubview(bannerImageView)
        bannerImageView.frame = CGRect(x: 0, y: 0, width: frame.width, height: 150)
        let bannerTap = UITapGestureRecognizer(target: self, action: #selector(bannerTapped))
        bannerImageView.addGestureRecognizer(bannerTap)
        bannerImageView.isUserInteractionEnabled = true

        // Blur effect
        addSubview(blurEffectView)
        blurEffectView.frame = bannerImageView.frame

        // Avatar with tap gesture
        addSubview(avatarView)
        avatarView.frame = CGRect(x: 16, y: 110, width: 80, height: 80)
        let avatarTap = UITapGestureRecognizer(target: self, action: #selector(avatarTapped))
        avatarView.addGestureRecognizer(avatarTap)
        avatarView.isUserInteractionEnabled = true

        // Follow Button
        addSubview(followButton)
        followButton.frame = CGRect(x: frame.width - 100, y: 160, width: 80, height: 30)

        // Name Label
        addSubview(nameLabel)
        nameLabel.frame = CGRect(x: 16, y: avatarView.frame.maxY + 10, width: frame.width - 32, height: 24)

        // Handle Label and Mark Stack
        addSubview(handleLabel)
        handleLabel.frame = CGRect(x: 16, y: nameLabel.frame.maxY + 4, width: frame.width - 32, height: 20)

        addSubview(markStackView)
        markStackView.frame = CGRect(x: handleLabel.frame.maxX + 4, y: nameLabel.frame.maxY + 4, width: 100, height: 20)

        // Follows/Fans Count with tap gesture
        addSubview(followsCountLabel)
        followsCountLabel.frame = CGRect(x: 16, y: handleLabel.frame.maxY + 6, width: 100, height: 20)
        let followsTap = UITapGestureRecognizer(target: self, action: #selector(followsCountTapped))
        followsCountLabel.addGestureRecognizer(followsTap)
        followsCountLabel.isUserInteractionEnabled = true

        addSubview(fansCountLabel)
        fansCountLabel.frame = CGRect(x: 120, y: handleLabel.frame.maxY + 6, width: 100, height: 20)
        let fansTap = UITapGestureRecognizer(target: self, action: #selector(fansCountTapped))
        fansCountLabel.addGestureRecognizer(fansTap)
        fansCountLabel.isUserInteractionEnabled = true

        // Description Label
        addSubview(descriptionLabel)

        descriptionLabel.frame = CGRect(x: 16, y: followsCountLabel.frame.maxY + 10, width: frame.width - 32, height: 0)
    }

    // 设置主题观察者
    private func setupThemeObserver() {
        // 移除旧的观察者（如果存在）
        if let existingObserver = themeObserver {
            NotificationCenter.default.removeObserver(existingObserver)
        }

        // 添加新的观察者
        themeObserver = NotificationCenter.default.addObserver(
            forName: NSNotification.Name("FlareThemeDidChange"),
            object: nil,
            queue: .main
        ) { [weak self] _ in
            self?.applyTheme()
        }

        // 立即应用当前主题
        applyTheme()
    }

    // 应用主题方法
    func applyTheme() {
        guard let theme else { return }

        // 应用背景色
        backgroundColor = UIColor(theme.primaryBackgroundColor)

        // 可以在这里应用其他与主题相关的样式
        nameLabel.textColor = UIColor(theme.labelColor)
        descriptionLabel.textColor = UIColor(theme.labelColor)
        descriptionLabel.backgroundColor = UIColor(theme.primaryBackgroundColor) // 貌似没用
        // 应用按钮颜色
        followButton.backgroundColor = UIColor(theme.tintColor)
    }

    private func layoutContent() {
        // 计算description的高度
        let descriptionWidth = frame.width - 32
        let descriptionSize = descriptionLabel.sizeThatFits(CGSize(width: descriptionWidth, height: .greatestFiniteMagnitude))

        // 更新description的frame
        descriptionLabel.frame = CGRect(x: 16, y: 280, width: descriptionWidth, height: descriptionSize.height)

        // 获取最后一个子视图的底部位置
        var maxY: CGFloat = 0
        for subview in subviews {
            let subviewBottom = subview.frame.maxY
            if subviewBottom > maxY {
                maxY = subviewBottom
            }
        }

        // 更新整体高度，添加底部padding
        frame.size.height = maxY + 16 // 16是底部padding
    }

    // 更新Banner拉伸效果
    func updateBannerStretch(withOffset offset: CGFloat) {
        let normalHeight: CGFloat = 150
        let stretchedHeight = normalHeight + max(0, offset)

        // 更新Banner图片frame
        bannerImageView.frame = CGRect(x: 0, y: min(0, -offset), width: frame.width, height: stretchedHeight)
        blurEffectView.frame = bannerImageView.frame

        // 根据拉伸程度设置模糊效果
        let blurAlpha = min(offset / 100, 0.3) // 最大模糊度0.3
        blurEffectView.alpha = blurAlpha
    }

    func getContentHeight() -> CGFloat {
        frame.height
    }

    func configure(with userInfo: ProfileUserInfo, state: ProfileNewState? = nil, theme: FlareTheme? = nil) {
        self.userInfo = userInfo // 需要保存 userInfo 以便在点击时使用
        self.state = state
        self.theme = theme

        // 应用主题
        if theme != nil {
            applyTheme()
        }

        // 设置用户名
        nameLabel.text = userInfo.profile.name.markdown

        // 设置用户handle
        handleLabel.text = "\(userInfo.profile.handleWithoutFirstAt)"

        if let url = URL(string: userInfo.profile.avatar) {
            avatarView.kf.setImage(
                with: url,
                options: FlareImageOptions.timelineAvatar(size: CGSize(width: 160, height: 160))
            )
        }

        // 设置banner - 使用 Kingfisher 缓存
        if let url = URL(string: userInfo.profile.banner ?? ""),
           !(userInfo.profile.banner ?? "").isEmpty,
           (userInfo.profile.banner ?? "").range(of: "^https?://.*example\\.com.*$", options: .regularExpression) == nil
        {
            bannerImageView.kf.setImage(
                with: url,
                options: FlareImageOptions.banner(size: CGSize(width: UIScreen.main.bounds.width * 2, height: 300))
            ) { result in
                switch result {
                case let .success(imageResult):
                    // 检查图片是否有效
                    if imageResult.image.size.width > 10, imageResult.image.size.height > 10 {
                        // 图片有效，保持现状
                    } else {
                        // 如果图片无效，使用头像作为背景
                        self.setupDynamicBannerBackground(avatarUrl: userInfo.profile.avatar)
                    }
                case .failure:
                    // 加载失败，使用头像作为背景
                    self.setupDynamicBannerBackground(avatarUrl: userInfo.profile.avatar)
                }
            }
        } else {
            // 如果没有banner，使用头像作为背景
            setupDynamicBannerBackground(avatarUrl: userInfo.profile.avatar)
        }

        // 设置关注/粉丝数
        followsCountLabel.text = "\(formatCount(Int64(userInfo.followCount) ?? 0)) \(NSLocalizedString("following_title", comment: ""))"
        fansCountLabel.text = "\(formatCount(Int64(userInfo.fansCount) ?? 0)) \(NSLocalizedString("fans_title", comment: ""))"

        // 更新关注按钮状态
        updateFollowButton(with: userInfo)

        // 设置用户标记
        markStackView.arrangedSubviews.forEach { $0.removeFromSuperview() }
        for mark in userInfo.profile.mark {
            let imageView = UIImageView()
            imageView.tintColor = .gray
            imageView.alpha = 0.6

            switch mark {
            case .cat:
                imageView.image = UIImage(systemName: "cat")
            case .verified:
                imageView.image = UIImage(systemName: "checkmark.circle.fill")
            case .locked:
                imageView.image = UIImage(systemName: "lock.fill")
            case .bot:
                imageView.image = UIImage(systemName: "cpu")
            default:
                continue
            }

            imageView.frame = CGRect(x: 0, y: 0, width: 16, height: 16)
            markStackView.addArrangedSubview(imageView)
        }

        // 开始流式布局，从关注/粉丝数下方开始
        var currentY = followsCountLabel.frame.maxY + 10

        // 设置描述文本
        if let description = userInfo.profile.description_?.markdown, !description.isEmpty {
            let descriptionView = UIHostingController(
                rootView: Markdown(description)
                    .markdownInlineImageProvider(.emoji)
            )
            if let theme {
                descriptionView.view.backgroundColor = UIColor(theme.primaryBackgroundColor)
            }
            addSubview(descriptionView.view)
            descriptionView.view.frame = CGRect(x: 16, y: currentY, width: frame.width - 32, height: 0)
            descriptionView.view.sizeToFit()
            currentY = descriptionView.view.frame.maxY + 16
        }

        if let bottomContent = userInfo.profile.bottomContent {
            switch onEnum(of: bottomContent) {
            case let .fields(data):
                // 设置个人的附加资料
                let fieldsView = UserInfoFieldsView(fields: data.fields)
                let hostingController = UIHostingController(rootView: fieldsView)
                hostingController.view.frame = CGRect(x: 16, y: currentY, width: frame.width - 32, height: 0)
                addSubview(hostingController.view)
                hostingController.view.sizeToFit()
                currentY = hostingController.view.frame.maxY + 16

            case let .iconify(data):
                let stackView = UIStackView()
                stackView.axis = .horizontal
                stackView.spacing = 8
                stackView.alignment = .leading
                stackView.distribution = .fill

                // 创建一个容器视图来包含所有内容
                let containerView = UIView()
                if let theme {
                    stackView.backgroundColor = UIColor(theme.primaryBackgroundColor)
                }
                containerView.addSubview(stackView)
                stackView.translatesAutoresizingMaskIntoConstraints = false

                // 设置 stackView 的约束
                NSLayoutConstraint.activate([
                    stackView.leadingAnchor.constraint(equalTo: containerView.leadingAnchor),
                    stackView.topAnchor.constraint(equalTo: containerView.topAnchor),
                    stackView.bottomAnchor.constraint(equalTo: containerView.bottomAnchor),
                    stackView.trailingAnchor.constraint(lessThanOrEqualTo: containerView.trailingAnchor),
                ])

                // 添加 location
                if let locationValue = data.items[.location] {
                    let locationView = createIconWithLabel(
                        icon: Asset.Image.Attributes.location.image,
                        text: locationValue.markdown
                    )
                    if let theme {
                        locationView.backgroundColor = UIColor(theme.primaryBackgroundColor)
                    }
                    stackView.addArrangedSubview(locationView)
                }

                // 添加 url
                if let urlValue = data.items[.url] {
                    let urlView = createIconWithLabel(
                        icon: Asset.Image.Attributes.globe.image,
                        text: urlValue.markdown
                    )
                    if let theme {
                        urlView.backgroundColor = UIColor(theme.primaryBackgroundColor)
                    }
                    stackView.addArrangedSubview(urlView)
                }

                containerView.frame = CGRect(x: 16, y: currentY, width: frame.width - 32, height: 20)
                addSubview(containerView)
                currentY = containerView.frame.maxY + 16
            }
        }

        // 更新视图总高度
        frame.size.height = currentY

        // 设置事件处理
        setupEventHandlers()
    }

    private func setupDynamicBannerBackground(avatarUrl: String?) {
        guard let avatarUrl, let url = URL(string: avatarUrl) else { return }

        bannerImageView.kf.setImage(
            with: url,
            options: FlareImageOptions.banner(size: CGSize(width: UIScreen.main.bounds.width * 2, height: 300))
        ) { [weak self] _ in
            self?.blurEffectView.alpha = 0.7 // 增加模糊效果
        }
    }

    private func updateFollowButton(with userInfo: ProfileUserInfo) {
        // 根据用户关系更新关注按钮状态
        if userInfo.isMe {
            followButton.isHidden = true
        } else {
            followButton.isHidden = false
            if let relation = userInfo.relation {
                let title = if relation.blocking {
                    NSLocalizedString("profile_header_button_blocked", comment: "")
                } else if relation.following {
                    NSLocalizedString("profile_header_button_following", comment: "")
                } else if relation.hasPendingFollowRequestFromYou {
                    NSLocalizedString("profile_header_button_requested", comment: "")
                } else {
                    NSLocalizedString("profile_header_button_follow", comment: "")
                }
                followButton.setTitle(title, for: .normal)

                // 保持蓝色背景
                // followButton.backgroundColor = .systemBlue
            }
        }
    }

    private func setupEventHandlers() {
        // 防止重复设置事件 - 检查是否已经有target
        let existingTargets = followButton.allTargets
        if !existingTargets.isEmpty {
            os_log("[ProfileNewHeaderView] Button events already setup, skipping", log: .default, type: .debug)
            return
        }

        // 确保按钮可以响应事件
        followButton.isEnabled = true
        followButton.isUserInteractionEnabled = true

        // 添加按钮事件
        followButton.addTarget(self, action: #selector(handleFollowButtonTap), for: .touchUpInside)

        os_log("[ProfileNewHeaderView] Button events setup completed", log: .default, type: .debug)
    }

    @objc private func avatarTapped() {
        onAvatarTap?()
    }

    @objc private func bannerTapped() {
        onBannerTap?()
    }

    @objc private func followsCountTapped() {
        onFollowsCountTap?()
    }

    @objc private func fansCountTapped() {
        onFansCountTap?()
    }

    @objc private func handleFollowButtonTap() {
        os_log("[ProfileNewHeaderView] Follow button tapped", log: .default, type: .debug)

        // 直接调用回调，传递 relation
        if let relation = userInfo?.relation {
            onFollowClick?(relation)
        }
    }

    // 辅助方法：查找当前视图所在的 ViewController
    private func findViewController() -> UIViewController? {
        var responder: UIResponder? = self
        while let nextResponder = responder?.next {
            if let viewController = nextResponder as? UIViewController {
                return viewController
            }
            responder = nextResponder
        }
        return nil
    }

    // Helper function to create icon with label
    private func createIconWithLabel(icon: UIImage, text: String) -> UIView {
        let hostingController = UIHostingController(
            rootView: Label(
                title: {
                    Markdown(text)
                        .font(.caption2)
                        .markdownInlineImageProvider(.emoji)
                        .lineLimit(1)
                },
                icon: {
                    Image(uiImage: icon.withRenderingMode(.alwaysTemplate))
                        .imageScale(.small)
                }
            )
            .labelStyle(CompactLabelStyle())
            .padding(.horizontal, 8)
            .padding(.vertical, 4)
            .cornerRadius(6)
            .onLongPressGesture {
                // 复制文本到剪贴板
                UIPasteboard.general.string = text

                // 显示复制成功提示
                let generator = UINotificationFeedbackGenerator()
                generator.notificationOccurred(.success)

                // 显示提示消息
                if let windowScene = UIApplication.shared.connectedScenes.first as? UIWindowScene,
                   let window = windowScene.windows.first
                {
                    let toast = UILabel()
                    toast.text = "copy to clipboard"
                    // toast.backgroundColor = UIColor.black.withAlphaComponent(0.7)
                    toast.textColor = .white
                    toast.textAlignment = .center
                    toast.font = UIFont.systemFont(ofSize: 14)
                    toast.layer.cornerRadius = 10
                    toast.clipsToBounds = true
                    toast.alpha = 0

                    window.addSubview(toast)
                    toast.translatesAutoresizingMaskIntoConstraints = false
                    NSLayoutConstraint.activate([
                        toast.centerXAnchor.constraint(equalTo: window.centerXAnchor),
                        toast.bottomAnchor.constraint(equalTo: window.safeAreaLayoutGuide.bottomAnchor, constant: -50),
                        toast.widthAnchor.constraint(greaterThanOrEqualToConstant: 150),
                        toast.heightAnchor.constraint(equalToConstant: 40),
                    ])

                    UIView.animate(withDuration: 0.3, animations: {
                        toast.alpha = 1
                    }, completion: { _ in
                        UIView.animate(withDuration: 0.3, delay: 1.5, options: [], animations: {
                            toast.alpha = 0
                        }, completion: { _ in
                            toast.removeFromSuperview()
                        })
                    })
                }
            }
        )
        hostingController.view.sizeToFit()
        return hostingController.view
    }
}
