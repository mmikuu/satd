import mysql.connector
from sentence_transformers import SentenceTransformer, util
import mysql.connector
from mysql.connector import Error
import pandas as pd
import Levenshtein
import csv
import pprint
import MySQLdb

def calc_bert(parent, child):
    emb1 = model.encode(parent.replace("\n", ""))
    emb2 = model.encode(child.replace("\n", ""))
    cos_sim = util.cos_sim(emb1, emb2).item()
    print("Cosine-Simmailarity:", cos_sim)
    return cos_sim


def calc_leven(parent, child):
    # calculation levenshtein
    str1 = parent
    str2 = child
    leven_sim = Levenshtein.distance(str1, str2)
    print("Levenshtein:", leven_sim)
    return leven_sim


def calc_leven_long(parent, child, leven_sim):
    leven_long = 0
    if len(child) < len(parent):
        leven_long = (len(parent) - leven_sim) / len(parent)
    elif len(parent) < len(child):
        leven_long = (len(child) - leven_sim) / len(child)
    else:
        leven_long = (len(parent) - leven_sim) / len(parent)
    print("LevenPer judge long charector", leven_long, "%\n")
    return leven_long


def get_hash_matched_data(cursor):
    hash_sql = ("SELECT "
       + "child_satd_list.id As CId, "
       + "child_satd_list.content As ChildContent, "
       + "parent_satd_list.id As PId, "
       + "parent_satd_list.content As ParentContent "
       + "FROM child_satd_list "
       + "INNER JOIN parent_satd_list "
       + "ON child_satd_list.hashcode = parent_satd_list.hashcode "
       + "AND child_satd_list.commitId = parent_satd_list.commitId")

    cursor.execute(hash_sql)
    hash_result = cursor.fetchall()
    print(hash_result)
    return hash_result


def creatTable(cursor):
    # 初期
    cursor.execute("DROP TABLE IF EXISTS satd_calc_list")

    # テーブルの作成
    cursor.execute("""CREATE TABLE satd_calc_list(
                   id INT(11) AUTO_INCREMENT NOT NULL,
                   pId INT(11) NOT NULL , 
                   cId INT(11) NOT NULL , 
                   pContent VARCHAR(1000) NOT NULL COLLATE utf8mb4_unicode_ci, 
                   cContent VARCHAR(1000) NOT NULL COLLATE utf8mb4_unicode_ci, 
                   calc_bert double precision NOT NULL,
                   calc_leven INT(20) NOT NUll,
                   calc_leven_long double precision NOT NULL ,
                   PRIMARY KEY (id)
                   )""")

def combination_data(cursor,isParent):
    comb_sql = ("SELECT "
       + "satd_calc_list.id As id, "
       +  "satd_calc_list.parent As parent, "
       + "satd_calc_list.child As child, "
       + "satd_calc_list.calc_bert As calc_bert, "
       + "satd_calc_list.calc_leven As calc_leven, "
       + "satd_calc_list.calc_leven_long As calc_leven_long, "
       + "replace_parent_matched_list.fileName As parent_fileName, "
       + "replace_parent_matched_list.commitId As parent_commitId, "
       + "FROM satd_calc_list "
       + "INNER JOIN replace_parent_matched_list ON satd_calc_list.parent = replace_parent_matched_list.content")

    if isParent:
        comb_sql = ("SELECT "
                    +"satd_calc_list.id As id, "
                    +"satd_calc_list.parent As parent, "
                    +"satd_calc_list.child As child, "
                    +"satd_calc_list.calc_bert As calc_bert, "
                    +"satd_calc_list.calc_leven As calc_leven, "
                    +"satd_calc_list.calc_leven_long As calc_leven_long, "
                    + "replace_child_matched_list.fileName As child_fileName, "
                    + "replace_child_matched_list.commitId As child_commitId "
                    +"FROM satd_calc_list "
                    +"INNER JOIN replace_child_matched_list ON satd_calc_list.parent = replace_child_matched_list.content")

    cursor.execute(comb_sql)
    result = cursor.fetchall()
    print(result[0])

# Press the green button in the gutter to run the script.
def addDataBase(cursor,parents,childs,parent,child,con_sim, leven_sim, leven_long):
    # Add data
    sql ="INSERT INTO satd_calc_list(pId, cId, pContent, cContent,calc_bert, calc_leven, calc_leven_long) VALUES(%s,%s,%s,%s,%s,%s,%s)"
    cursor.execute(sql, (parents, childs, parent, child, con_sim, leven_sim, leven_long))
    connection.commit()


def getId(dict,value):
    for k,v in dict.items():
        if value == v:
            return k

import sys

if __name__ == '__main__':
    args = sys.argv
    if(len(args)==1):
        db = 'satd_replace_db'
    else:
        db = 'test_satd_replace_db'

    model = SentenceTransformer('all-mpnet-base-v2')

    # mysql connect
    connection = MySQLdb.connect(
        host='localhost',
        user='me',
        passwd='goma',
        db=db)
    cursor = connection.cursor()

    # generate result database
    creatTable(cursor)
    # get combination matched parentSatd and childSatd
    hash_results = get_hash_matched_data(cursor)

    for hash_result in hash_results:
        cId = hash_result[0]
        cContent = hash_result[1]
        pId = hash_result[2]
        pContent = hash_result[3]

        #calculate
        con_sim = calc_bert(pContent, cContent)
        leven_sim = calc_leven(pContent, cContent)
        leven_long = calc_leven_long(pContent, cContent, leven_sim)

        addDataBase(cursor,pId,cId,pContent,cContent,con_sim,leven_sim,leven_long)


        


