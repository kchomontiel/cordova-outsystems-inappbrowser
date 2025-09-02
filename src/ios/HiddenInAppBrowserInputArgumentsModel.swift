import Foundation

struct HiddenInAppBrowserInputArgumentsSimpleModel: Decodable {
    let url: String
    
    enum CodingKeys: String, CodingKey {
        case url
    }
    
    init(from decoder: Decoder) throws {
        let container = try decoder.container(keyedBy: CodingKeys.self)
        url = try container.decode(String.self, forKey: .url)
    }
    
    init(url: String) {
        self.url = url
    }
}


