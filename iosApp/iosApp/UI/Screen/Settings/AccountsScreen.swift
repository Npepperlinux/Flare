import SwiftUI
import shared

struct AccountsScreen: View {
    @State var viewModel = AccountsViewModel()
    var body: some View {
        List {
            switch onEnum(of: viewModel.model.accounts) {
            case .success(let data):
                if data.data.size > 0 {
                    ForEach(1...data.data.size, id: \.self) { index in
                        let item = data.data.get(index: index - 1)
                        switch onEnum(of: item) {
                        case .success(let user):
                            Button {
                                viewModel.model.setActiveAccount(accountKey: user.data.userKey)
                            } label: {
                                HStack {
                                    UserComponent(user: user.data)
                                    Spacer()
                                    switch onEnum(of: viewModel.model.activeAccount) {
                                    case .success(let activeAccount):
                                        Image(systemName: activeAccount.data.accountKey == user.data.userKey ? "checkmark.circle.fill" : "circle")
                                            .foregroundStyle(.blue)
                                    default:
                                        Image(systemName: "circle")
                                            .foregroundStyle(.blue)
                                    }
                                }.swipeActions(edge: .trailing) {
                                    Button(role: .destructive) {
                                        viewModel.model.removeAccount(accountKey: user.data.userKey)
                                    } label: {
                                        Label("Delete", systemImage: "trash")
                                    }
                                }
                            }

                        case .error:
                            Text("error")
                        case .loading:
                            Text("loading")
                        }
                    }
                } else {
                    Text("No Accounts")
                }
            case .error:
                Text("error")
            case .loading:
                Text("loading")
            }
        }
        .navigationTitle("Accounts")
        .toolbar {
            NavigationLink(value: SheetDestination.serviceSelection) {
                Image(systemName: "plus")
            }
        }
        .activateViewModel(viewModel: viewModel)
    }
}

@Observable
class AccountsViewModel : MoleculeViewModelBase<AccountsState, AccountsPresenter> {
    
}

#Preview {
    NavigationStack {
        AccountsScreen()
    }
}