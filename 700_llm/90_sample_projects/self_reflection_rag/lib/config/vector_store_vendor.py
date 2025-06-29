from enum import Enum


class VectorStoreProp:
    def __init__(self, vendor_name, dump_sub_dir, dump_file):
        self._vendor_name = vendor_name
        self._dump_sub_dir = dump_sub_dir
        self._dump_file = dump_file

    @property
    def vendor_name(self):
        return self._vendor_name

    @property
    def dump_file(self):
        return self._dump_file

    @property
    def dump_sub_dir(self):
        return self._dump_sub_dir


class VectorStoreVendor(Enum):
    FAISS = 1,


vectorstore_prop_map = {
    VectorStoreVendor.FAISS: VectorStoreProp(
        vendor_name="FAISS",
        dump_sub_dir="faiss_dump",
        dump_file="index"),
}


def get_vectorstore_prop(vendor: VectorStoreVendor) -> VectorStoreProp:
    return vectorstore_prop_map.get(vendor)
