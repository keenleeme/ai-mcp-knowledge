import os
import requests
import pdfkit

WIKI_URL = "https://wiki.das-security.cn"
PAGE_ID = "67588238"
COOKIES = {"JSESSIONID": "FE168A65A8856C949A6E08333DDD25E1"}

API_BASE = f"{WIKI_URL}/rest/api/content"

BASE_DIR = os.path.abspath(os.path.join(os.path.dirname(__file__), "..", "..", ".."))
PDF_DIR = os.path.join(BASE_DIR, "data", "pdf")
os.makedirs(PDF_DIR, exist_ok=True)


def get_page_tree(root_page_id):
    session = requests.Session()
    session.cookies.update(COOKIES)
    visited = set()
    pages = []

    def walk(page_id):
        if page_id in visited:
            return
        visited.add(page_id)
        url = f"{API_BASE}/{page_id}?expand=children.page"
        res = session.get(url)
        res.raise_for_status()
        data = res.json()
        pages.append({"id": data["id"], "title": data["title"]})
        children = data.get("children", {}).get("page", {})
        for child in children.get("results", []):
            walk(child["id"])

    walk(root_page_id)
    return pages, session


def html_to_pdf(html, output_path):
    options = {
        "encoding": "UTF-8",
        "quiet": "",
    }
    pdfkit.from_string(html, output_path, options=options)


def export_all_pages():
    pages, session = get_page_tree(PAGE_ID)
    for page in pages:
        if page["id"] == PAGE_ID:
            continue
        page_id = page["id"]
        title = page["title"].replace("/", "_").replace("\\", "_")
        filename = os.path.join(PDF_DIR, f"{title}_{page_id}.pdf")
        url = f"{API_BASE}/{page_id}?expand=body.export_view"
        try:
            res = session.get(url)
            res.raise_for_status()
            data = res.json()
            html = data.get("body", {}).get("export_view", {}).get("value", "")
            if not html:
                print(f"页面内容为空，跳过: {page_id} {title}")
                continue
            html_to_pdf(html, filename)
            print(f"已导出: {filename}")
        except Exception as e:
            print(f"导出失败，已跳过: {page_id} {title}，错误: {e}")


if __name__ == "__main__":
    export_all_pages()
