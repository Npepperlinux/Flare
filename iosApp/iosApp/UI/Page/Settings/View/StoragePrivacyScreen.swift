import Kingfisher
import shared
import SwiftUI
import Tiercel

struct StoragePrivacyScreen: View {
    @State private var presenter = StoragePresenter()
    @State private var imageCacheSize: String = "Calculating..."
    @State private var isCleaningCache = false
    @Environment(FlareTheme.self) private var theme

    // 下载相关状态
    @State private var downloadFilesInfo: String = "Calculating..."
    @State private var isCleaningDownloadFiles = false

    // 对话框状态
    @State private var showClearFilesConfirm = false
    @State private var showSuccessAlert = false
    @State private var successMessage = ""

    private func calculateImageCacheSize() {
        ImageCache.default.calculateDiskStorageSize { result in
            switch result {
            case let .success(size):
                imageCacheSize = StorageFormatter.formatFileSize(Int64(size))
            case .failure:
                imageCacheSize = "calculate failed"
            }
        }
    }

    private func updateDownloadSizes() {
        let tasks = DownloadManager.shared.tasks
        let taskCount = tasks.count
        let totalSize = tasks.reduce(0) { total, task in
            total + Int(task.progress.totalUnitCount)
        }
        downloadFilesInfo = StorageFormatter.formatDownloadInfo(fileCount: taskCount, totalSize: Int64(totalSize))
    }

    private func clearImageCache() {
        isCleaningCache = true
        ImageCache.default.clearDiskCache {
            ImageCache.default.clearMemoryCache()
            calculateImageCacheSize()
            isCleaningCache = false
            showSuccessAlert(message: "image cache cleaned")
        }
    }

    private func clearDownloadFiles() {
        isCleaningDownloadFiles = true

        DownloadManager.shared.clearAllTasks(completely: true)

        updateDownloadSizes()
        isCleaningDownloadFiles = false
        showSuccessAlert(message: "download files cleaned")
    }

    private func showSuccessAlert(message: String) {
        successMessage = message
        showSuccessAlert = true
    }

    var body: some View {
        ObservePresenter(presenter: presenter) { state in
            List {
                // Storage Management Section
                Section("Storage Management") {
                    // Database Cache
                    Button(role: .destructive) {
                        state.clearCache()
                    } label: {
                        HStack(alignment: .center) {
                            Image(systemName: "trash")
                                .font(.title)
                                .foregroundColor(theme.tintColor)
                            Spacer()
                                .frame(width: 16)
                            VStack(alignment: .leading) {
                                Text("Clear Database Cache")
                                Text("Clear \(state.userCount) users and \(state.statusCount) posts data")
                                    .font(.caption)
                                    .foregroundColor(.secondary)
                            }
                        }
                    }
                    .buttonStyle(.borderless)

                    // Image Cache
                    Button(role: .destructive) {
                        clearImageCache()
                    } label: {
                        HStack {
                            Image(systemName: "photo.circle")
                                .font(.title)
                                .foregroundColor(theme.tintColor)
                            Spacer()
                                .frame(width: 16)
                            VStack(alignment: .leading) {
                                Text("Clear Image Cache")
                                Text("Cache size: \(imageCacheSize)")
                                    .font(.caption)
                                    .foregroundColor(.secondary)
                            }
                            if isCleaningCache {
                                Spacer()
                                ProgressView()
                            }
                        }
                    }
                    .buttonStyle(.borderless)
                    .disabled(isCleaningCache)

                    // Download Cache
                    Button(role: .destructive) {
                        showClearFilesConfirm = true
                    } label: {
                        HStack {
                            Image(systemName: "folder")
                                .font(.title)
                                .foregroundColor(theme.tintColor)
                            Spacer()
                                .frame(width: 16)
                            VStack(alignment: .leading) {
                                Text("Clean Download Files")
                                Text("\(downloadFilesInfo)")
                                    .font(.caption)
                                    .foregroundColor(.secondary)
                            }
                            if isCleaningDownloadFiles {
                                Spacer()
                                ProgressView()
                            }
                        }
                    }
                    .buttonStyle(.borderless)
                    .disabled(isCleaningDownloadFiles)
                }.listRowBackground(theme.primaryBackgroundColor)

                // Privacy Settings Section
                Section("Privacy Settings") {
                    // 这里可以添加未来的隐私设置
                    // 目前隐私相关的设置（如敏感内容分析）已经移到了其他页面
                    Text("Privacy settings will be added here in future updates")
                        .font(.caption)
                        .foregroundColor(.secondary)
                }.listRowBackground(theme.primaryBackgroundColor)
            }
            .background(theme.secondaryBackgroundColor)
            .navigationTitle("Storage & Privacy")
            .navigationBarTitleDisplayMode(.inline)
            .onAppear {
                calculateImageCacheSize()
                updateDownloadSizes()
            }
            .confirmationDialog(
                title: "Clean Download Files",
                message: "Are you sure you want to delete all downloaded files? This action cannot be undone.",
                isPresented: $showClearFilesConfirm,
                action: clearDownloadFiles
            )
            .successToast(successMessage, isPresented: $showSuccessAlert)
        }
    }
}
